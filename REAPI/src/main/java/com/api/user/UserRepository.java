package com.api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.api.enums.MasterEnums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {


	Optional<User> findByEmail(String email);


	Optional<User> findByMobile(String mobile);


	boolean existsByEmail(String email);


	boolean existsByMobile(String mobile);


	long countByUserPackage(MasterEnums.PackageEnum userPackage);


	/**
	 * Paginated admin search: filters by plan (optional) and a free-text
	 * search across name/email/mobile (optional). All filters null = list
	 * everything ordered by id desc.
	 */
	@Query("""
		SELECT u FROM User u
		WHERE (:plan   IS NULL OR u.userPackage = :plan)
		  AND (:search IS NULL
		       OR LOWER(u.name)   LIKE CONCAT('%', LOWER(:search), '%')
		       OR LOWER(u.email)  LIKE CONCAT('%', LOWER(:search), '%')
		       OR LOWER(u.mobile) LIKE CONCAT('%', LOWER(:search), '%'))
		ORDER BY u.id DESC
		""")
	Page<User> adminSearch(
		@Param("plan") MasterEnums.PackageEnum plan,
		@Param("search") String search,
		Pageable pageable);

	/**
	 * Locks the user row {@code SELECT ... FOR UPDATE} so concurrent posts
	 * by the same user serialise on the limit check. Without this two
	 * parallel adds at {@code count = limit - 1} could both pass.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.id = :id")
	Optional<User> findByIdForUpdate(@Param("id") Long id);
}
