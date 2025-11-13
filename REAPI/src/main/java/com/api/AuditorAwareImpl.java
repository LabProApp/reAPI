package com.api;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		// ⚙️ Example 1: Get from Spring Security
		// If using authentication:
		/*
		 * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		 * if (auth != null && auth.isAuthenticated() &&
		 * !"anonymousUser".equals(auth.getName())) { return
		 * Optional.of(auth.getName()); }
		 */

		// ⚙️ Example 2: Fallback (system or API service account)
		return Optional.of("system");
	}
}
