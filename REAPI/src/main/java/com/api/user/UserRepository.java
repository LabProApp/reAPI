package com.api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides standard CRUD operations via {@link JpaRepository} as well as
 * custom lookup methods used during authentication, signup duplicate-checking,
 * and OTP-based verification flows.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Looks up a user by their unique email address.
	 *
	 * @param email the email address to search for
	 * @return an {@link Optional} containing the matching {@link User}, or empty if not found
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Looks up a user by their unique mobile phone number.
	 *
	 * @param mobile the mobile number to search for
	 * @return an {@link Optional} containing the matching {@link User}, or empty if not found
	 */
	Optional<User> findByMobile(String mobile);

	/**
	 * Checks whether a user with the given email address already exists.
	 *
	 * @param email the email address to check
	 * @return {@code true} if a user with this email exists, {@code false} otherwise
	 */
	boolean existsByEmail(String email);

	/**
	 * Checks whether a user with the given mobile phone number already exists.
	 *
	 * @param mobile the mobile number to check
	 * @return {@code true} if a user with this mobile exists, {@code false} otherwise
	 */
	boolean existsByMobile(String mobile);
}
