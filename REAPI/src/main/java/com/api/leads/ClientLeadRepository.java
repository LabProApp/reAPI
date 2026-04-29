package com.api.leads;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link ClientLead} entities. Extends
 * {@link JpaSpecificationExecutor} to support dynamic, criteria-based
 * filtering via {@link ClientLeadSpecification}. Provides derived-query
 * methods for common access patterns.
 */
@Repository
public interface ClientLeadRepository extends JpaRepository<ClientLead, Long>, JpaSpecificationExecutor<ClientLead> {

	/**
	 * Returns all leads associated with the specified property.
	 *
	 * @param propertyId the ID of the property to query leads for
	 * @return a list of {@link ClientLead} instances; empty if none exist
	 */
	List<ClientLead> findByPropertyId(Long propertyId);

	/**
	 * Checks whether a lead already exists for the given user–property
	 * combination, enforcing the unique constraint at the application level
	 * before attempting a persist.
	 *
	 * @param userId     the ID of the client user
	 * @param propertyId the ID of the property
	 * @return {@code true} if a lead already exists for this user and property
	 */
	boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
}
