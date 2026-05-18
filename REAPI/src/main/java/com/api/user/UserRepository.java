package com.api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface UserRepository extends JpaRepository<User, Long> {


	Optional<User> findByEmail(String email);


	Optional<User> findByMobile(String mobile);


	boolean existsByEmail(String email);


	boolean existsByMobile(String mobile);

	/**
	 * Locks the user row {@code SELECT ... FOR UPDATE} so concurrent posts
	 * by the same user serialise on the limit check. Without this two
	 * parallel adds at {@code count = limit - 1} could both pass.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.id = :id")
	Optional<User> findByIdForUpdate(@Param("id") Long id);
}
