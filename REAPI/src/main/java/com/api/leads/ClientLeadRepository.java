package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientLeadRepository extends JpaRepository<ClientLead, Long> {

	List<ClientLead> findByUserId(Long userId);

	List<ClientLead> findByBrokerId(Long brokerId);

	List<ClientLead> findByStatus(String status);

	List<ClientLead> findByContacted(Boolean contacted);

	List<ClientLead> findByBrokerIdAndStatusIn(Long brokerId, List<String> status);

	List<ClientLead> findByBrokerIdAndInquiryDateBetween(Long brokerId, LocalDateTime start, LocalDateTime end);

	List<ClientLead> findByBrokerIdAndStatusInAndInquiryDateBetween(Long brokerId, List<String> status,
			LocalDateTime start, LocalDateTime end);
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
}
