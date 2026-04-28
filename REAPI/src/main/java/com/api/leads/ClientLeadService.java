package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.NotificationService;
import com.api.prop.Property;
import com.api.prop.PropertyRepository;
import com.api.user.User;
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

	public ClientLeadDTO createLead(ClientLeadDTO dto) {
		log.info("createLead - userId={}, propertyId={}", dto.getUserId(), dto.getPropertyId());

		if (dto.getUserId() != null && dto.getPropertyId() != null
				&& repository.existsByUserIdAndPropertyId(dto.getUserId(), dto.getPropertyId())) {
			log.warn("createLead - Duplicate lead for userId={}, propertyId={}", dto.getUserId(), dto.getPropertyId());
			throw new RuntimeException("Lead already exists for this customer and property");
		}

		ClientLead entity = mapper.map(dto, ClientLead.class);
		entity.setStatus(MasterEnums.LeadStatus.NEW);

		// Auto-populate property snapshot and owner from Property entity
		String brokerEmail = null, brokerMobile = null;
		String ownerEmail = null, ownerMobile = null;

		if (dto.getPropertyId() != null) {
			propertyRepository.findById(dto.getPropertyId()).ifPresent(p -> {
				entity.setPropertyTitle(p.getTitle());
				entity.setPropertyCity(p.getCity());
				entity.setPropertyPrice(p.getPrice());
				entity.setPropertyType(p.getType());
				entity.setPropertyOwnerId(p.getPostedByUser());
			});

			// Fetch broker (brokerId) contact details for notification
			Property prop = propertyRepository.findById(dto.getPropertyId()).orElse(null);
			if (prop != null) {
				// Try to resolve broker contact from User table if brokerId is set
				if (dto.getBrokerId() != null) {
					User broker = userRepository.findById(dto.getBrokerId()).orElse(null);
					if (broker != null) {
						entity.setBrokerId(broker.getId());
					}
				}
				// Resolve owner contact for notifications
				if (prop.getPostedByUser() != null) {
					userRepository.findById(prop.getPostedByUser()).ifPresent(owner -> {
						// owner contact stored in local vars via the array trick below
					});
				}
			}
		}

		// Auto-populate customer info from User entity
		if (dto.getUserId() != null) {
			userRepository.findById(dto.getUserId()).ifPresent(u -> {
				if (entity.getClientName() == null || entity.getClientName().isBlank()) entity.setClientName(u.getName());
				if (entity.getMobile() == null || entity.getMobile().isBlank()) entity.setMobile(u.getMobile());
				if (entity.getEmail() == null || entity.getEmail().isBlank()) entity.setEmail(u.getEmail());
			});
		}

		ClientLead saved = repository.save(entity);
		log.info("createLead - Lead created with id={}", saved.getId());

		// Resolve broker/owner contact for notifications (after save)
		String[] brokerContact = resolveContact(saved.getBrokerId());
		String[] ownerContact = resolveContact(saved.getPropertyOwnerId());

		notificationService.notifyPropertyInquiry(
				saved.getId(),
				saved.getClientName(), saved.getMobile(), saved.getEmail(),
				saved.getPropertyTitle(), saved.getPropertyCity(), saved.getPropertyPrice(),
				brokerContact[0], brokerContact[1],
				ownerContact[0], ownerContact[1],
				dto.getPreferredBudget(), saved.getMessage(), dto.isSendWhatsApp());

		return mapper.map(saved, ClientLeadDTO.class);
	}

	public ClientLeadDTO updateLead(Long id, ClientLeadDTO dto) {
		log.info("updateLead - id={}", id);
		ClientLead existing = getEntityById(id);
		mapper.map(dto, existing);
		ClientLead updated = repository.save(existing);
		log.info("updateLead - Lead id={} updated", id);
		return mapper.map(updated, ClientLeadDTO.class);
	}

	public ClientLeadDTO updateStatus(Long id, MasterEnums.LeadStatus status, String remark) {
		log.info("updateStatus - leadId={}, status={}", id, status);
		ClientLead lead = getEntityById(id);
		MasterEnums.LeadStatus previous = lead.getStatus();
		lead.setStatus(status);
		if (remark != null && !remark.isBlank()) lead.setRemark(remark);
		if (status == MasterEnums.LeadStatus.CONTACTED && lead.getContactedDate() == null) {
			lead.setContactedDate(LocalDateTime.now());
		}
		ClientLead saved = repository.save(lead);
		log.info("updateStatus - leadId={} status {} → {}", id, previous, status);

		// Notify customer of status change
		notificationService.notifyLeadStatusUpdated(
				saved.getClientName(), saved.getMobile(), saved.getEmail(),
				saved.getPropertyTitle(), saved.getPropertyCity(),
				status.name(), remark);

		return mapper.map(saved, ClientLeadDTO.class);
	}

	public ClientLeadDTO scheduleFollowUp(Long id, LocalDateTime followUpDate, String remark) {
		log.info("scheduleFollowUp - leadId={}, followUpDate={}", id, followUpDate);
		ClientLead lead = getEntityById(id);
		lead.setNextFollowUpDate(followUpDate);
		if (remark != null && !remark.isBlank()) lead.setRemark(remark);
		ClientLead saved = repository.save(lead);
		log.info("scheduleFollowUp - leadId={} followUp set to {}", id, followUpDate);
		return mapper.map(saved, ClientLeadDTO.class);
	}

	public ClientLeadDTO getById(Long id) {
		log.info("getById - id={}", id);
		return mapper.map(getEntityById(id), ClientLeadDTO.class);
	}

	public void delete(Long id) {
		log.info("delete - id={}", id);
		repository.deleteById(id);
		log.info("delete - Lead id={} deleted", id);
	}

	public List<ClientLeadDTO> getByUserId(Long userId) {
		log.info("getByUserId - userId={}", userId);
		return toDto(repository.findByUserId(userId));
	}

	public List<ClientLeadDTO> getByPropertyId(Long propertyId) {
		log.info("getByPropertyId - propertyId={}", propertyId);
		return toDto(repository.findByPropertyId(propertyId));
	}

	public List<ClientLeadDTO> getByPropertyOwnerId(Long ownerId) {
		log.info("getByPropertyOwnerId - ownerId={}", ownerId);
		return toDto(repository.findByPropertyOwnerId(ownerId));
	}

	public List<ClientLeadDTO> getByBrokerWithFilters(Long brokerId, List<MasterEnums.LeadStatus> status,
			LocalDateTime startDate, LocalDateTime endDate) {
		log.info("getByBrokerWithFilters - brokerId={} [status={}, start={}, end={}]", brokerId, status, startDate, endDate);
		List<ClientLead> leads;
		boolean hasStatus = status != null && !status.isEmpty();
		boolean hasDates = startDate != null && endDate != null;

		if (!hasStatus && !hasDates) {
			leads = repository.findByBrokerId(brokerId);
		} else if (hasStatus && !hasDates) {
			leads = repository.findByBrokerIdAndStatusIn(brokerId, status);
		} else if (!hasStatus) {
			leads = repository.findByBrokerIdAndInquiryDateBetween(brokerId, startDate, endDate);
		} else {
			leads = repository.findByBrokerIdAndStatusInAndInquiryDateBetween(brokerId, status, startDate, endDate);
		}
		log.info("getByBrokerWithFilters - Returned {} leads for brokerId={}", leads.size(), brokerId);
		return toDto(leads);
	}

	public List<ClientLeadDTO> getByStatus(MasterEnums.LeadStatus status) {
		log.info("getByStatus - status={}", status);
		return toDto(repository.findByStatus(status));
	}

	// ─── Helpers ─────────────────────────────────────────────────────────────

	private ClientLead getEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> {
			log.error("getEntityById - Lead not found for id={}", id);
			return new RuntimeException("Lead not found with id: " + id);
		});
	}

	private List<ClientLeadDTO> toDto(List<ClientLead> leads) {
		return leads.stream().map(e -> mapper.map(e, ClientLeadDTO.class)).collect(Collectors.toList());
	}

	/** Returns [email, mobile] for a userId, or ["", ""] if not found. */
	private String[] resolveContact(Long userId) {
		if (userId == null) return new String[]{"", ""};
		return userRepository.findById(userId)
				.map(u -> new String[]{u.getEmail() != null ? u.getEmail() : "", u.getMobile() != null ? u.getMobile() : ""})
				.orElse(new String[]{"", ""});
	}
}
