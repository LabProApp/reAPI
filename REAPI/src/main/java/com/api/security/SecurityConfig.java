package com.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Real Estate API.
 *
 * <p>Configures a stateless, JWT-based security model:</p>
 * <ul>
 *   <li>CSRF protection is disabled — the API is stateless and does not use
 *       browser sessions or cookies for authentication.</li>
 *   <li>Session creation policy is set to {@link SessionCreationPolicy#STATELESS}
 *       so that no HTTP session is ever created or used.</li>
 *   <li>The {@link JwtAuthFilter} is inserted before Spring's default
 *       {@link UsernamePasswordAuthenticationFilter} to authenticate requests
 *       via the {@code Authorization: Bearer <token>} header.</li>
 * </ul>
 *
 * <p><b>Public routes</b> (no token required):</p>
 * <ul>
 *   <li>{@code /api/user/signup}</li>
 *   <li>{@code /api/user/login}</li>
 *   <li>{@code /api/user/verifyOtp}</li>
 *   <li>{@code /api/user/resend-otp}</li>
 *   <li>{@code /api/user/reset-password}</li>
 *   <li>{@code /v3/api-docs/**} — OpenAPI spec</li>
 *   <li>{@code /swagger-ui/**} and {@code /swagger-ui.html} — Swagger UI</li>
 *   <li>{@code /javadoc/**} — hosted JavaDoc</li>
 * </ul>
 *
 * <p>All other routes require a valid JWT.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	/**
	 * Constructs a {@code SecurityConfig} with the required JWT authentication filter.
	 *
	 * @param jwtAuthFilter the filter that validates JWT tokens on each request
	 */
	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	/**
	 * Defines the main {@link SecurityFilterChain} that governs HTTP security
	 * for the application.
	 *
	 * @param http the {@link HttpSecurity} builder provided by Spring Security
	 * @return the configured {@link SecurityFilterChain}
	 * @throws Exception if an error occurs while building the security configuration
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/user/signup",
					"/api/user/login",
					"/api/user/verifyOtp",
					"/api/user/resend-otp",
					"/api/user/reset-password",
					"/v3/api-docs/**",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/javadoc/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Exposes a {@link BCryptPasswordEncoder} as the application's
	 * {@link PasswordEncoder} bean, used when hashing and verifying user passwords.
	 *
	 * @return a {@link BCryptPasswordEncoder} instance
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
