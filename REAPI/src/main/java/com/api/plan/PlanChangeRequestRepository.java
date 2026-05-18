package com.api.plan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanChangeRequestRepository extends JpaRepository<PlanChangeRequest, Long> {

	List<PlanChangeRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

	List<PlanChangeRequest> findByStatusOrderByRequestedAtAsc(PlanChangeRequestStatus status);

	/** Used to enforce "at most one PENDING request per user" in the service. */
	Optional<PlanChangeRequest> findFirstByUserIdAndStatus(Long userId, PlanChangeRequestStatus status);
}
