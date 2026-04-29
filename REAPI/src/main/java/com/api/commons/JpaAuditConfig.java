package com.api.commons;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring configuration class that activates JPA auditing for the application.
 *
 * <p>Enables {@code @CreatedDate}, {@code @LastModifiedDate}, {@code @CreatedBy},
 * and {@code @LastModifiedBy} population on entities that extend {@link BaseEntity}.
 * The auditor (current principal) is resolved by the {@link AuditorAwareImpl} bean,
 * registered under the name {@code "auditorAware"}.</p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditConfig {
}
