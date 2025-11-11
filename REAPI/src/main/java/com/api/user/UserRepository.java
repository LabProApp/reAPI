package com.api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByMobile(String mobile);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);
}