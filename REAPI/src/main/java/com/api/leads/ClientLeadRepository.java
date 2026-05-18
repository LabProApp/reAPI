package com.api.leads;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientLeadRepository extends JpaRepository<ClientLead, Long>, JpaSpecificationExecutor<ClientLead> {

	List<ClientLead> findByPropertyId(Long propertyId);

	boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

	Optional<ClientLead> findByUserIdAndPropertyId(Long userId, Long propertyId);

	// Fallback for anonymous leads (no userId): match on mobile + property
	Optional<ClientLead> findFirstByMobileAndPropertyId(String mobile, Long propertyId);
}
