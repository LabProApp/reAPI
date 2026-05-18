package com.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helpers for pulling identity off the SecurityContext that
 * {@link JwtAuthFilter} populates.
 */
public final class AuthUtils {

	private AuthUtils() {}

	/**
	 * Returns the userId carried by the current request's JWT, or null if
	 * no JWT was presented (e.g. on a {@code permitAll()} endpoint).
	 */
	public static Long currentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) return null;
		Object principal = auth.getPrincipal();
		if (principal instanceof Long id) return id;
		if (principal instanceof String s) {
			try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; }
		}
		return null;
	}

	/** True if the current request's JWT has the given role (without ROLE_ prefix). */
	public static boolean hasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) return false;
		String target = "ROLE_" + role;
		for (GrantedAuthority ga : auth.getAuthorities()) {
			if (target.equals(ga.getAuthority())) return true;
		}
		return false;
	}
}
