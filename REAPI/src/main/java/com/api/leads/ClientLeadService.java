package com.api.leads;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.NotificationService;
import com.api.notifications.events.LeadGeneratedEvent;
import com.api.prop.PropertyRepository;
import com.api.user.User;
import com.api.user.UserRepository;

@Service
@Transactional
public class ClientLeadService {

	private static final Logger log = LoggerFactory.getLogger(ClientLeadService.class);

	private final ClientLeadRepository repository;
	private final ModelMapper mapper;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;
	private final ApplicationEventPublisher eventPublisher;


	public ClientLeadService(ClientLeadRepository repository, ModelMapper mapper,
			PropertyRepository propertyRepository, UserRepository userRepository,
			NotificationService notificationService, ApplicationEventPublisher eventPublisher) {
		this.repository = repository;
		this.mapper = mapper;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
		this.eventPublisher = eventPublisher;
	}

	// ─── Create ───────────────────────────────────────────────────────────────

	
	public ClientLeadDTO createLead(ClientLeadDTO dto) {
		log.info("createLead - leadType={}, userId={}, propertyId={}", dto.getLeadType(), dto.getUserId(), dto.getPropertyId());

		// Duplicate detection — return the existing lead (with a friendly flag) instead of erroring out.
		ClientLead existing = findDuplicate(dto);
		if (existing != null) {
			log.info("createLead - duplicate inquiry detected, returning existing lead id={}", existing.getId());
			ClientLeadDTO out = mapper.map(existing, ClientLeadDTO.class);
			out.setInquiryAlreadySent(true);
			out.setResponseMessage("Inquiry already sent");
			return out;
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

		// Fire notifications via event — NotificationEventListener handles routing
		String[] brokerContact = resolveContact(saved.getBrokerId());
		String[] ownerContact = resolveContact(saved.getPropertyOwnerId());
		eventPublisher.publishEvent(new LeadGeneratedEvent(saved,
				brokerContact[0], brokerContact[1],
				ownerContact[0], ownerContact[1],
				dto.isSendWhatsApp()));

		return mapper.map(saved, ClientLeadDTO.class);
	}

	// ─── Update ───────────────────────────────────────────────────────────────

	
	public ClientLeadDTO updateLead(Long id, ClientLeadDTO dto) {
		log.info("updateLead - id={}", id);
		ClientLead existing = getEntityById(id);
		mapper.map(dto, existing);
		existing.setId(id);
		ClientLead updated = repository.save(existing);
		log.info("updateLead - Lead id={} updated", id);
		return mapper.map(updated, ClientLeadDTO.class);
	}

	
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

	
	public ClientLeadDTO updateApproval(Long id, String approvedBank, Double approvedLoanAmount, Double approvedInterestRate) {
		log.info("updateApproval - leadId={}, bank={}", id, approvedBank);
		ClientLead lead = getEntityById(id);
		lead.setApprovedBank(approvedBank);
		lead.setApprovedLoanAmount(approvedLoanAmount);
		lead.setApprovedInterestRate(approvedInterestRate);
		return mapper.map(repository.save(lead), ClientLeadDTO.class);
	}

	
	public ClientLeadDTO scheduleFollowUp(Long id, LocalDateTime followUpDate, String remark) {
		log.info("scheduleFollowUp - leadId={}, followUpDate={}", id, followUpDate);
		ClientLead lead = getEntityById(id);
		lead.setNextFollowUpDate(followUpDate);
		if (!blank(remark)) lead.setRemark(remark);
		return mapper.map(repository.save(lead), ClientLeadDTO.class);
	}

	// ─── Read ─────────────────────────────────────────────────────────────────

	
	public ClientLeadDTO getById(Long id) {
		return mapper.map(getEntityById(id), ClientLeadDTO.class);
	}

	
	public List<ClientLeadDTO> search(
			Long brokerId, Long ownerId, Long propertyId, Long userId,
			List<MasterEnums.LeadStatus> statuses, MasterEnums.InquiryType leadType,
			String mobile, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("search - brokerId={}, ownerId={}, propertyId={}, userId={}, leadType={}, statuses={}",
				brokerId, ownerId, propertyId, userId, leadType, statuses);
		List<ClientLead> results = repository.findAll(
				ClientLeadSpecification.build(brokerId, ownerId, propertyId, userId, statuses, leadType, mobile, startDate, endDate));
		log.info("search - returned {} leads", results.size());
		return enrichLeads(results);
	}

	
	public List<ClientLeadDTO> getLeadSummariesByPropertyId(Long propertyId) {
		log.info("getLeadSummariesByPropertyId - propertyId={}", propertyId);
		return enrichLeads(repository.findByPropertyId(propertyId));
	}

	
	public void delete(Long id) {
		log.info("delete - id={}", id);
		repository.deleteById(id);
	}

	// ─── Helpers ─────────────────────────────────────────────────────────────

	
	private ClientLead getEntityById(Long id) {
		return repository.findById(id).orElseThrow(() ->
				new RuntimeException("Lead not found with id: " + id));
	}

	// Returns the existing lead if the same inquiry has already been submitted,
	// otherwise null. Match precedence: (userId + propertyId) > (mobile + propertyId).
	private ClientLead findDuplicate(ClientLeadDTO dto) {
		if (dto.getPropertyId() == null) return null;
		if (dto.getUserId() != null) {
			return repository.findByUserIdAndPropertyId(dto.getUserId(), dto.getPropertyId()).orElse(null);
		}
		if (dto.getMobile() != null && !dto.getMobile().isBlank()) {
			return repository.findFirstByMobileAndPropertyId(dto.getMobile(), dto.getPropertyId()).orElse(null);
		}
		return null;
	}

	
	// Batch-loads all owner/broker users in one IN query instead of per-lead lookups.
	private List<ClientLeadDTO> enrichLeads(List<ClientLead> leads) {
		Set<Long> userIds = new HashSet<>();
		for (ClientLead l : leads) {
			if (l.getPropertyOwnerId() != null) userIds.add(l.getPropertyOwnerId());
			if (l.getBrokerId() != null) userIds.add(l.getBrokerId());
		}
		Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
				.collect(Collectors.toMap(User::getId, u -> u));

		return leads.stream().map(lead -> {
			ClientLeadDTO dto = mapper.map(lead, ClientLeadDTO.class);
			User owner = userMap.get(lead.getPropertyOwnerId());
			if (owner != null) {
				dto.setOwnerName(owner.getName());
				dto.setOwnerMobile(owner.getMobile());
				dto.setOwnerEmail(owner.getEmail());
			}
			User broker = userMap.get(lead.getBrokerId());
			if (broker != null) dto.setBrokerName(broker.getName());
			return dto;
		}).collect(Collectors.toList());
	}

	
	private String[] resolveContact(Long userId) {
		if (userId == null) return new String[]{"", ""};
		return userRepository.findById(userId)
				.map(u -> new String[]{
						u.getEmail() != null ? u.getEmail() : "",
						u.getMobile() != null ? u.getMobile() : ""})
				.orElse(new String[]{"", ""});
	}

	
	private boolean blank(String s) {
		return s == null || s.isBlank();
	}
}
