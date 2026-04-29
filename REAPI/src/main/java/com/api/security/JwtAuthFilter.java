package com.api.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet filter that intercepts every HTTP request exactly once and validates
 * the JWT Bearer token present in the {@code Authorization} header.
 *
 * <p>When a valid token is found the filter extracts the user ID and role from
 * the token's claims and populates the Spring Security
 * {@link org.springframework.security.core.context.SecurityContext} with an
 * authenticated {@link UsernamePasswordAuthenticationToken}. Requests without a
 * token, or with an invalid / expired token, are passed through the filter chain
 * without authentication so that Spring Security's access rules can deny them
 * where appropriate.</p>
 *
 * <p>The filter is registered before
 * {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
 * in the security filter chain configured in {@link SecurityConfig}.</p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	/**
	 * Constructs a {@code JwtAuthFilter} with the required JWT utility.
	 *
	 * @param jwtUtil the component used to validate tokens and extract their claims
	 */
	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Processes a single HTTP request, attempting JWT authentication if a
	 * {@code Bearer} token is present in the {@code Authorization} header.
	 *
	 * <p>Processing steps:</p>
	 * <ol>
	 *   <li>Read the {@code Authorization} header; if absent or not prefixed
	 *       with {@code "Bearer "}, pass the request straight through.</li>
	 *   <li>Validate the extracted token via {@link JwtUtil#isTokenValid(String)};
	 *       if invalid, pass the request through unauthenticated.</li>
	 *   <li>Extract the user ID and role from the token claims and set a
	 *       fully-authenticated {@link UsernamePasswordAuthenticationToken}
	 *       on the {@link SecurityContextHolder}.</li>
	 *   <li>Continue the filter chain.</li>
	 * </ol>
	 *
	 * @param request     the incoming HTTP request
	 * @param response    the HTTP response
	 * @param filterChain the remaining filter chain to delegate to
	 * @throws ServletException if a servlet error occurs during filtering
	 * @throws IOException      if an I/O error occurs during filtering
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		if (!jwtUtil.isTokenValid(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		Claims claims = jwtUtil.extractClaims(token);
		Long userId = Long.parseLong(claims.getSubject());
		String role = claims.get("role", String.class);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
