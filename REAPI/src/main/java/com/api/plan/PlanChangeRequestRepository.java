package com.api.plan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PlanChangeRequestRepository extends JpaRepository<PlanChangeRequest, Long> {

	List<PlanChangeRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

	List<PlanChangeRequest> findByStatusOrderByRequestedAtAsc(PlanChangeRequestStatus status);

	/** Used to enforce "at most one PENDING request per user" in the service. */
	Optional<PlanChangeRequest> findFirstByUserIdAndStatus(Long userId, PlanChangeRequestStatus status);

	/**
	 * Locks the request row {@code SELECT ... FOR UPDATE} so two admins
	 * (or one admin double-clicking) can't both transition the same
	 * PENDING request out of PENDING. The second caller waits, sees the
	 * new status, and is rejected by the "not PENDING anymore" guard.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM PlanChangeRequest r WHERE r.id = :id")
	Optional<PlanChangeRequest> findByIdForUpdate(@Param("id") Long id);
}
