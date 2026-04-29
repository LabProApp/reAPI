package com.api.leads;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientLeadRepository extends JpaRepository<ClientLead, Long>, JpaSpecificationExecutor<ClientLead> {

	List<ClientLead> findByPropertyId(Long propertyId);

	boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
}
