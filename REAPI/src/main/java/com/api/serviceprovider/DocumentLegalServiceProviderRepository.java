package com.api.serviceprovider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for {@link DocumentLegalServiceProvider} entities.
 *
 * <p>Extends both {@link JpaRepository} for standard CRUD operations and
 * {@link JpaSpecificationExecutor} for dynamic query composition via JPA
 * {@link org.springframework.data.jpa.domain.Specification} predicates,
 * enabling the flexible search used by {@link DocumentLegalServiceProviderService}.</p>
 */
public interface DocumentLegalServiceProviderRepository extends JpaRepository<DocumentLegalServiceProvider, Long>,
		JpaSpecificationExecutor<DocumentLegalServiceProvider> {

}
