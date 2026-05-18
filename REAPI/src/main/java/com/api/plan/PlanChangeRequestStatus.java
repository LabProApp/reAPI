package com.api.plan;

/**
 * Lifecycle of a user-submitted plan change request.
 *
 * <pre>
 *   PENDING  --(admin approve)--> APPROVED
 *   PENDING  --(admin reject)---> REJECTED
 *   PENDING  --(user cancel)----> CANCELLED
 * </pre>
 *
 * Terminal states (APPROVED / REJECTED / CANCELLED) are never re-entered.
 */
public enum PlanChangeRequestStatus {
	PENDING,
	APPROVED,
	REJECTED,
	CANCELLED
}
