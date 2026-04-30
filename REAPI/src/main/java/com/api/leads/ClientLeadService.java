package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.NotificationService;
import com.api.prop.PropertyRepository;
import com.api.user.UserRepository;

/**
 * Service layer for all client lead operations. Manages the full lifecycle of
 * a {@link ClientLead} — creation, status transitions, approval recording,
 * follow-up scheduling, search, and deletion. Enriches read results with
 * resolved owner and broker display names from the User repository and fires
 * asynchronous notifications via {@link NotificationService}.
 *
 * <p>All public methods are wrapped in a transaction via the class-level
 * {@link Transactional} annotation.
 */
@Service
@Transactional
public class ClientLeadService {

	private static final Logger log = LoggerFactory.getLogger(ClientLeadService.class);

	private final ClientLeadRepository repository;
	private final ModelMapper mapper;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;

	/**
	 * Constructs a {@code ClientLeadService} with its required collaborators.
	 *
	 * @param repository           the JPA repository for {@link ClientLead} entities
	 * @param mapper               the ModelMapper instance used for DTO/entity mapping
	 * @param propertyRepository   repository used to auto-populate property snapshots
	 * @param userRepository       repository used to resolve user, owner, and broker details
	 * @param notificationService  service used to fire lead-related notifications
	 */
	public ClientLeadService(ClientLeadRepository repository, ModelMapper mapper,
			PropertyRepository propertyRepository, UserRepository userRepository,
			NotificationService notificationService) {
		this.repository = repository;
		this.mapper = mapper;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}

	// ─── Create ───────────────────────────────────────────────────────────────

	/**
	 * Creates a new client lead. If a lead already exists for the same
	 * {@code userId} and {@code propertyId} combination, a
	 * {@link RuntimeException} is thrown. Client info is auto-populated from the
	 * User entity when {@code userId} is supplied, and property snapshot fields
	 * are auto-populated from the Property entity when {@code propertyId} is
	 * supplied. After saving, broker and owner contacts are resolved and passed
	 * to {@link NotificationService#notifyLeadCreated}.
	 *
	 * @param dto the lead details from the request body; {@code clientName},
	 *            {@code mobile}, and {@code leadType} are mandatory
	 * @return the persisted lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if a duplicate lead already exists for the
	 *                          given user and property
	 */
	public ClientLeadDTO createLead(ClientLeadDTO dto) {
		log.info("createLead - leadType={}, userId={}, propertyId={}", dto.getLeadType(), dto.getUserId(), dto.getPropertyId());

		if (dto.getUserId() != null && dto.getPropertyId() != null
				&& repository.existsByUserIdAndPropertyId(dto.getUserId(), dto.getPropertyId())) {
			throw new RuntimeException("Lead already exists for this customer and property");
		}

		ClientLead entity = mapper.map(dto, ClientLead.class);
		entity.setStatus(MasterEnums.LeadStatus.NEW);

		// Auto-populate customer info from User if userId provided
		if (dto.getUserId() != null) {
			userRepository.findById(dto.getUserId()).ifPresent(u -> {
				if (blank(entity.getClientName())) entity.setClientName(u.getName());
				if (blank(entity.getMobile())) entity.setMobile(u.getMobile());
				if (blank(entity.getEmail())) entity.setEmail(u.getEmail());
			});
		}

		// Auto-populate property snapshot and owner if propertyId provided
		if (dto.getPropertyId() != null) {
			propertyRepository.findById(dto.getPropertyId()).ifPresent(p -> {
				if (blank(entity.getPropertyTitle())) entity.setPropertyTitle(p.getTitle());
				if (blank(entity.getPropertyCity())) entity.setPropertyCity(p.getCity());
				if (blank(entity.getPropertyState())) entity.setPropertyState(p.getState());
				if (entity.getPropertyPrice() == null) entity.setPropertyPrice(p.getPrice());
				if (blank(entity.getPropertyType())) entity.setPropertyType(p.getType());
				entity.setPropertyOwnerId(p.getPostedByUser());
			});
		}

		ClientLead saved = repository.save(entity);
		log.info("createLead - Lead id={} created, type={}", saved.getId(), saved.getLeadType());

		// Fire notifications async
		String[] brokerContact = resolveContact(saved.getBrokerId());
		String[] ownerContact = resolveContact(saved.getPropertyOwnerId());
		notificationService.notifyLeadCreated(saved,
				brokerContact[0], brokerContact[1],
				ownerContact[0], ownerContact[1],
				dto.isSendWhatsApp());

		return mapper.map(saved, ClientLeadDTO.class);
	}

	// ─── Update ───────────────────────────────────────────────────────────────

	/**
	 * Fully updates an existing lead by mapping all fields from the supplied DTO
	 * onto the persisted entity, preserving the original ID.
	 *
	 * @param id  the ID of the lead to update
	 * @param dto the updated lead data
	 * @return the updated lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if no lead is found with the given ID
	 */
	public ClientLeadDTO updateLead(Long id, ClientLeadDTO dto) {
		log.info("updateLead - id={}", id);
		ClientLead existing = getEntityById(id);
		mapper.map(dto, existing);
		existing.setId(id);
		ClientLead updated = repository.save(existing);
		log.info("updateLead - Lead id={} updated", id);
		return mapper.map(updated, ClientLeadDTO.class);
	}

	/**
	 * Transitions the status of a lead and optionally records a remark. If the
	 * new status is {@link MasterEnums.LeadStatus#CONTACTED} and no contacted
	 * date has been set yet, the current timestamp is recorded. A status-change
	 * notification is dispatched after saving.
	 *
	 * @param id     the ID of the lead whose status is to be updated
	 * @param status the new status value
	 * @param remark an optional internal remark about the status change
	 * @return the updated lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if no lead is found with the given ID
	 */
	public ClientLeadDTO updateStatus(Long id, MasterEnums.LeadStatus status, String remark) {
		log.info("updateStatus - leadId={}, newStatus={}", id, status);
		ClientLead lead = getEntityById(id);
		lead.setStatus(status);
		if (!blank(remark)) lead.setRemark(remark);
		if (status == MasterEnums.LeadStatus.CONTACTED && lead.getContactedDate() == null) {
			lead.setContactedDate(LocalDateTime.now());
		}
		ClientLead saved = repository.save(lead);
		notificationService.notifyLeadStatusUpdated(
				saved.getClientName(), saved.getMobile(), saved.getEmail(),
				saved.getPropertyTitle(), saved.getPropertyCity(),
				status.name(), remark);
		log.info("updateStatus - leadId={} updated to {}", id, status);
		return mapper.map(saved, ClientLeadDTO.class);
	}

	/**
	 * Records loan approval details on an existing lead after the bank has
	 * processed the application.
	 *
	 * @param id                   the ID of the lead to update
	 * @param approvedBank         the name of the bank that approved the loan
	 * @param approvedLoanAmount   the approved loan amount
	 * @param approvedInterestRate the annual interest rate at which the loan was approved
	 * @return the updated lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if no lead is found with the given ID
	 */
	public ClientLeadDTO updateApproval(Long id, String approvedBank, Double approvedLoanAmount, Double approvedInterestRate) {
		log.info("updateApproval - leadId={}, bank={}", id, approvedBank);
		ClientLead lead = getEntityById(id);
		lead.setApprovedBank(approvedBank);
		lead.setApprovedLoanAmount(approvedLoanAmount);
		lead.setApprovedInterestRate(approvedInterestRate);
		return mapper.map(repository.save(lead), ClientLeadDTO.class);
	}

	/**
	 * Schedules or updates the next follow-up date for a lead and optionally
	 * records a remark.
	 *
	 * @param id           the ID of the lead to update
	 * @param followUpDate the next follow-up timestamp
	 * @param remark       an optional remark about the follow-up
	 * @return the updated lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if no lead is found with the given ID
	 */
	public ClientLeadDTO scheduleFollowUp(Long id, LocalDateTime followUpDate, String remark) {
		log.info("scheduleFollowUp - leadId={}, followUpDate={}", id, followUpDate);
		ClientLead lead = getEntityById(id);
		lead.setNextFollowUpDate(followUpDate);
		if (!blank(remark)) lead.setRemark(remark);
		return mapper.map(repository.save(lead), ClientLeadDTO.class);
	}

	// ─── Read ─────────────────────────────────────────────────────────────────

	/**
	 * Retrieves a single lead by its ID.
	 *
	 * @param id the ID of the lead to retrieve
	 * @return the lead mapped to a {@link ClientLeadDTO}
	 * @throws RuntimeException if no lead is found with the given ID
	 */
	public ClientLeadDTO getById(Long id) {
		return mapper.map(getEntityById(id), ClientLeadDTO.class);
	}

	/**
	 * Searches for leads using any combination of the supplied filter criteria.
	 * Delegates predicate construction to {@link ClientLeadSpecification#build}
	 * and enriches each result with resolved owner and broker display names.
	 * Results are ordered newest-first by inquiry date.
	 *
	 * @param brokerId   filter by broker ID; {@code null} to skip
	 * @param ownerId    filter by property owner ID; {@code null} to skip
	 * @param propertyId filter by property ID; {@code null} to skip
	 * @param userId     filter by client user ID; {@code null} to skip
	 * @param statuses   filter by one or more lead statuses; {@code null} or empty to skip
	 * @param leadType   filter by inquiry type; {@code null} to skip
	 * @param mobile     partial mobile number match; {@code null} or blank to skip
	 * @param startDate  include only leads with an inquiry date on or after this value;
	 *                   {@code null} to skip
	 * @param endDate    include only leads with an inquiry date on or before this value;
	 *                   {@code null} to skip
	 * @return a list of enriched {@link ClientLeadDTO} instances matching all supplied criteria
	 */
	public List<ClientLeadDTO> search(
			Long brokerId, Long ownerId, Long propertyId, Long userId,
			List<MasterEnums.LeadStatus> statuses, MasterEnums.InquiryType leadType,
			String mobile, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("search - brokerId={}, ownerId={}, propertyId={}, userId={}, leadType={}, statuses={}",
				brokerId, ownerId, propertyId, userId, leadType, statuses);
		List<ClientLead> results = repository.findAll(
				ClientLeadSpecification.build(brokerId, ownerId, propertyId, userId, statuses, leadType, mobile, startDate, endDate));
		log.info("search - returned {} leads", results.size());
		return results.stream().map(this::toEnrichedDto).collect(Collectors.toList());
	}

	/**
	 * Returns an enriched lead summary for all leads associated with the given
	 * property, including resolved owner and broker display names.
	 *
	 * @param propertyId the ID of the property whose leads are to be retrieved
	 * @return a list of enriched {@link ClientLeadDTO} instances; empty if none exist
	 */
	public List<ClientLeadDTO> getLeadSummariesByPropertyId(Long propertyId) {
		log.info("getLeadSummariesByPropertyId - propertyId={}", propertyId);
		return repository.findByPropertyId(propertyId).stream()
				.map(this::toEnrichedDto)
				.collect(Collectors.toList());
	}

	/**
	 * Deletes the lead with the given ID.
	 *
	 * @param id the ID of the lead to delete
	 */
	public void delete(Long id) {
		log.info("delete - id={}", id);
		repository.deleteById(id);
	}

	// ─── Helpers ─────────────────────────────────────────────────────────────

	/**
	 * Retrieves a {@link ClientLead} entity by ID, throwing a
	 * {@link RuntimeException} if it does not exist.
	 *
	 * @param id the ID of the lead to fetch
	 * @return the found {@link ClientLead} entity
	 * @throws RuntimeException if no lead exists with the given ID
	 */
	private ClientLead getEntityById(Long id) {
		return repository.findById(id).orElseThrow(() ->
				new RuntimeException("Lead not found with id: " + id));
	}

	/**
	 * Converts a {@link ClientLead} entity to a {@link ClientLeadDTO} and
	 * enriches the result with the resolved display names of the property owner
	 * and the broker, looked up from the User repository.
	 *
	 * @param lead the entity to enrich
	 * @return an enriched {@link ClientLeadDTO} with owner and broker details populated
	 */
	private ClientLeadDTO toEnrichedDto(ClientLead lead) {
		ClientLeadDTO dto = mapper.map(lead, ClientLeadDTO.class);
		if (lead.getPropertyOwnerId() != null) {
			userRepository.findById(lead.getPropertyOwnerId()).ifPresent(u -> {
				dto.setOwnerName(u.getName());
				dto.setOwnerMobile(u.getMobile());
				dto.setOwnerEmail(u.getEmail());
			});
		}
		if (lead.getBrokerId() != null) {
			userRepository.findById(lead.getBrokerId()).ifPresent(u -> dto.setBrokerName(u.getName()));
		}
		return dto;
	}

	/**
	 * Resolves the email and mobile of a user by their ID. Returns an array of
	 * two empty strings if the ID is {@code null} or the user is not found.
	 *
	 * @param userId the ID of the user whose contact details are to be resolved
	 * @return a two-element array {@code [email, mobile]}; elements are empty
	 *         strings when the user cannot be resolved
	 */
	private String[] resolveContact(Long userId) {
		if (userId == null) return new String[]{"", ""};
		return userRepository.findById(userId)
				.map(u -> new String[]{
						u.getEmail() != null ? u.getEmail() : "",
						u.getMobile() != null ? u.getMobile() : ""})
				.orElse(new String[]{"", ""});
	}

	/**
	 * Returns {@code true} if the given string is {@code null} or blank.
	 *
	 * @param s the string to test
	 * @return {@code true} if {@code s} is null or contains only whitespace
	 */
	private boolean blank(String s) {
		return s == null || s.isBlank();
	}
}
