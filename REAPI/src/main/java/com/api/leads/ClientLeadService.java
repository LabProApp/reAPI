package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.NotificationService;
import com.api.prop.PropertyRepository;
import com.api.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ClientLeadService {

	private final ClientLeadRepository repository;
	private final ModelMapper mapper;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;

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

	public List<ClientLeadSummaryDTO> search(
			Long brokerId, Long ownerId, Long propertyId, Long userId,
			List<MasterEnums.LeadStatus> statuses, MasterEnums.InquiryType leadType,
			String mobile, LocalDateTime startDate, LocalDateTime endDate) {
		log.info("search - brokerId={}, ownerId={}, propertyId={}, userId={}, leadType={}, statuses={}",
				brokerId, ownerId, propertyId, userId, leadType, statuses);
		List<ClientLead> results = repository.findAll(
				ClientLeadSpecification.build(brokerId, ownerId, propertyId, userId, statuses, leadType, mobile, startDate, endDate));
		log.info("search - returned {} leads", results.size());
		return results.stream().map(this::toSummary).collect(Collectors.toList());
	}

	public List<ClientLeadSummaryDTO> getLeadSummariesByPropertyId(Long propertyId) {
		log.info("getLeadSummariesByPropertyId - propertyId={}", propertyId);
		return repository.findByPropertyId(propertyId).stream()
				.map(this::toSummary)
				.collect(Collectors.toList());
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

	private ClientLeadSummaryDTO toSummary(ClientLead lead) {
		ClientLeadSummaryDTO dto = new ClientLeadSummaryDTO();
		dto.setId(lead.getId());
		dto.setLeadType(lead.getLeadType());
		dto.setStatus(lead.getStatus());
		dto.setLeadSource(lead.getLeadSource());
		dto.setClientName(lead.getClientName());
		dto.setMobile(lead.getMobile());
		dto.setEmail(lead.getEmail());
		dto.setProfession(lead.getProfession());
		dto.setMonthlyIncome(lead.getMonthlyIncome());
		dto.setPropertyId(lead.getPropertyId());
		dto.setPropertyOwnerId(lead.getPropertyOwnerId());
		dto.setBrokerId(lead.getBrokerId());
		dto.setAssignedAgentName(lead.getAssignedAgentName());
		dto.setPropertyTitle(lead.getPropertyTitle());
		dto.setPropertyCity(lead.getPropertyCity());
		dto.setPropertyState(lead.getPropertyState());
		dto.setPropertyLocality(lead.getPropertyLocality());
		dto.setPropertyType(lead.getPropertyType());
		dto.setPropertyPrice(lead.getPropertyPrice());
		dto.setBudget(lead.getBudget());
		dto.setMinBudget(lead.getMinBudget());
		dto.setMaxBudget(lead.getMaxBudget());
		dto.setRequiredLoanAmount(lead.getRequiredLoanAmount());
		dto.setLoanTenureYears(lead.getLoanTenureYears());
		dto.setLoanType(lead.getLoanType());
		dto.setPreferredBank(lead.getPreferredBank());
		dto.setDocumentServicesRequired(lead.getDocumentServicesRequired());
		dto.setSpecifications(lead.getSpecifications());
		dto.setMessage(lead.getMessage());
		dto.setRemark(lead.getRemark());
		dto.setInquiryDate(lead.getInquiryDate());
		dto.setContactedDate(lead.getContactedDate());
		dto.setNextFollowUpDate(lead.getNextFollowUpDate());
		dto.setExpectedPurchaseDate(lead.getExpectedPurchaseDate());

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
