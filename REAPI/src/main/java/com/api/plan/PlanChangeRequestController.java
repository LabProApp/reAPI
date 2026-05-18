package com.api.plan;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.security.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/plan-requests")
@Tag(name = "Plan Change Requests",
		description = "User-side plan upgrade requests with admin review queue")
public class PlanChangeRequestController {

	private static final Logger log = LoggerFactory.getLogger(PlanChangeRequestController.class);

	private final PlanChangeRequestService service;

	public PlanChangeRequestController(PlanChangeRequestService service) {
		this.service = service;
	}

	// ── User endpoints ──────────────────────────────────────────────────────

	@Operation(summary = "Submit a plan upgrade request")
	@PostMapping
	public ResponseEntity<PlanChangeRequestDto> submit(@RequestBody PlanChangeRequestDto body) {
		Long userId = AuthUtils.currentUserId();
		if (userId == null) return ResponseEntity.status(401).build();
		log.info("POST /api/plan-requests - userId={} plan={}", userId, body.getRequestedPlan());
		return ResponseEntity.ok(service.submit(userId, body.getRequestedPlan(), body.getNotes()));
	}

	@Operation(summary = "List the current user's own request history")
	@GetMapping("/me")
	public ResponseEntity<List<PlanChangeRequestDto>> mine() {
		Long userId = AuthUtils.currentUserId();
		if (userId == null) return ResponseEntity.status(401).build();
		return ResponseEntity.ok(service.getMyRequests(userId));
	}

	@Operation(summary = "Cancel one of the current user's own PENDING requests")
	@PostMapping("/{id}/cancel")
	public ResponseEntity<PlanChangeRequestDto> cancel(@PathVariable Long id) {
		Long userId = AuthUtils.currentUserId();
		if (userId == null) return ResponseEntity.status(401).build();
		return ResponseEntity.ok(service.cancel(id, userId));
	}

	// ── Admin endpoints ─────────────────────────────────────────────────────

	@Operation(summary = "ADMIN: list requests by status (defaults to PENDING)")
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<PlanChangeRequestDto>> listByStatus(
			@RequestParam(required = false) PlanChangeRequestStatus status) {
		return ResponseEntity.ok(service.listByStatus(status));
	}

	@Operation(summary = "ADMIN: approve a PENDING request and apply the plan")
	@PostMapping("/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PlanChangeRequestDto> approve(@PathVariable Long id,
			@RequestParam(required = false) Integer durationYears) {
		Long adminUserId = AuthUtils.currentUserId();
		return ResponseEntity.ok(service.approve(id, adminUserId, durationYears));
	}

	@Operation(summary = "ADMIN: reject a PENDING request with a reason")
	@PostMapping("/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PlanChangeRequestDto> reject(@PathVariable Long id,
			@RequestBody Map<String, String> body) {
		Long adminUserId = AuthUtils.currentUserId();
		return ResponseEntity.ok(service.reject(id, adminUserId, body.get("reason")));
	}
}
