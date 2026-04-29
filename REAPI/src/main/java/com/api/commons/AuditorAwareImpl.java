package com.api.commons;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Data JPA {@link AuditorAware} implementation that resolves the current
 * auditor (the principal performing a write operation) from the Spring Security
 * context.
 *
 * <p>When a valid, non-anonymous authentication is present the auditor is the
 * string representation of {@link Authentication#getPrincipal()} (typically the
 * numeric user ID stored in the JWT subject claim). If no authenticated user is
 * available — for example during system-initiated operations or unauthenticated
 * bootstrap tasks — the literal string {@code "system"} is returned instead.</p>
 *
 * <p>This bean is referenced by name ({@code "auditorAware"}) in
 * {@link JpaAuditConfig} via {@code @EnableJpaAuditing(auditorAwareRef = "auditorAware")}.</p>
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

	/**
	 * Returns the identifier of the currently authenticated principal, or
	 * {@code "system"} when no authenticated user is present in the security context.
	 *
	 * @return an {@link Optional} containing the principal's string representation,
	 *         or {@code Optional.of("system")} for unauthenticated / anonymous requests
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
			return Optional.of(String.valueOf(auth.getPrincipal()));
		}
		return Optional.of("system");
	}
}
