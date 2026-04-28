package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.enums.MasterEnums;

@Repository
public interface ClientLeadRepository extends JpaRepository<ClientLead, Long> {

	List<ClientLead> findByUserId(Long userId);

	List<ClientLead> findByBrokerId(Long brokerId);

	List<ClientLead> findByPropertyId(Long propertyId);

	List<ClientLead> findByPropertyOwnerId(Long propertyOwnerId);

	List<ClientLead> findByStatus(MasterEnums.LeadStatus status);

	List<ClientLead> findByBrokerIdAndStatusIn(Long brokerId, List<MasterEnums.LeadStatus> status);

	List<ClientLead> findByBrokerIdAndInquiryDateBetween(Long brokerId, LocalDateTime start, LocalDateTime end);

	List<ClientLead> findByBrokerIdAndStatusInAndInquiryDateBetween(Long brokerId, List<MasterEnums.LeadStatus> status,
			LocalDateTime start, LocalDateTime end);

	boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
}
