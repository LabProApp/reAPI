package com.api.serviceprovider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentLegalServiceProviderRepository extends JpaRepository<DocumentLegalServiceProvider, Long>,
		JpaSpecificationExecutor<DocumentLegalServiceProvider> {
	// Additional query methods (optional)
}