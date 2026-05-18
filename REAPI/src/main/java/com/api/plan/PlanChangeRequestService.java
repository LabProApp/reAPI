package com.api.plan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.CommService;
import com.api.user.User;
import com.api.user.UserRepository;
import com.api.user.UserService;

/**
 * User-side plan-change workflow. Users submit requests; admins approve
 * or reject. Approval delegates to {@link UserService#changePlan} so the
 * subscription window is stamped consistently with admin-initiated
 * assignments.
 */
@Service
public class PlanChangeRequestService {

	private static final Logger log = LoggerFactory.getLogger(PlanChangeRequestService.class);

	private final PlanChangeRequestRepository repository;
	private final UserService userService;
	private final UserRepository userRepository;
	private final CommService commService;

	public PlanChangeRequestService(PlanChangeRequestRepository repository,
			UserService userService, UserRepository userRepository, CommService commService) {
		this.repository = repository;
		this.userService = userService;
		this.userRepository = userRepository;
		this.commService = commService;
	}

	/**
	 * User submits a request to upgrade. Rejects if:
	 * <ul>
	 *   <li>they already have a PENDING request,</li>
	 *   <li>the requested plan is null or BASIC (no "request" needed for free).</li>
	 * </ul>
	 */
	@Transactional
	public PlanChangeRequestDto submit(Long userId, MasterEnums.PackageEnum requestedPlan, String notes) {
		if (requestedPlan == null) {
			throw new IllegalArgumentException("requestedPlan is required");
		}
		if (requestedPlan == MasterEnums.PackageEnum.BASIC) {
			throw new IllegalArgumentException("BASIC is free — no request needed");
		}
		repository.findFirstByUserIdAndStatus(userId, PlanChangeRequestStatus.PENDING)
			.ifPresent(r -> {
				throw new IllegalArgumentException(
					"You already have a pending plan request (id=" + r.getId() + ")");
			});

		PlanChangeRequest req = new PlanChangeRequest();
		req.setUserId(userId);
		req.setRequestedPlan(requestedPlan);
		req.setStatus(PlanChangeRequestStatus.PENDING);
		req.setNotes(trim(notes));
		req.setRequestedAt(LocalDateTime.now());
		PlanChangeRequest saved = repository.save(req);
		log.info("submit - userId={} requestedPlan={} requestId={}", userId, requestedPlan, saved.getId());
		return PlanChangeRequestDto.from(saved);
	}

	/** Returns the user's full request history, newest first. */
	public List<PlanChangeRequestDto> getMyRequests(Long userId) {
		return repository.findByUserIdOrderByRequestedAtDesc(userId).stream()
			.map(PlanChangeRequestDto::from)
			.toList();
	}

	/** Admin queue view — pending first by default. */
	public List<PlanChangeRequestDto> listByStatus(PlanChangeRequestStatus status) {
		PlanChangeRequestStatus s = status == null ? PlanChangeRequestStatus.PENDING : status;
		return repository.findByStatusOrderByRequestedAtAsc(s).stream()
			.map(PlanChangeRequestDto::from)
			.toList();
	}

	/**
	 * Admin approves a PENDING request, applying the requested plan to the user.
	 *
	 * <p>Uses a pessimistic write lock on the request row so two admins
	 * approving the same request simultaneously serialise — the second one
	 * waits, sees the row in APPROVED state, and is rejected by the
	 * not-PENDING guard.</p>
	 */
	@Transactional
	public PlanChangeRequestDto approve(Long requestId, Long adminUserId, Integer durationYears) {
		PlanChangeRequest req = loadPendingForUpdate(requestId);
		userService.changePlan(req.getUserId(), req.getRequestedPlan(), durationYears,
				"Approved plan-change request #" + req.getId());
		req.setStatus(PlanChangeRequestStatus.APPROVED);
		req.setReviewedAt(LocalDateTime.now());
		req.setReviewedBy(adminUserId);
		PlanChangeRequest saved = repository.save(req);
		log.info("approve - requestId={} userId={} plan={} adminUserId={}",
				saved.getId(), saved.getUserId(), saved.getRequestedPlan(), adminUserId);
		notifyApproved(saved);
		return PlanChangeRequestDto.from(saved);
	}

	/** Admin rejects a PENDING request with a reason. */
	@Transactional
	public PlanChangeRequestDto reject(Long requestId, Long adminUserId, String reason) {
		PlanChangeRequest req = loadPendingForUpdate(requestId);
		req.setStatus(PlanChangeRequestStatus.REJECTED);
		req.setRejectionReason(trim(reason));
		req.setReviewedAt(LocalDateTime.now());
		req.setReviewedBy(adminUserId);
		PlanChangeRequest saved = repository.save(req);
		log.info("reject - requestId={} userId={} reason='{}' adminUserId={}",
				saved.getId(), saved.getUserId(), reason, adminUserId);
		notifyRejected(saved);
		return PlanChangeRequestDto.from(saved);
	}

	/** A user cancels their own PENDING request. */
	@Transactional
	public PlanChangeRequestDto cancel(Long requestId, Long userId) {
		PlanChangeRequest req = repository.findByIdForUpdate(requestId)
			.orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));
		if (!req.getUserId().equals(userId)) {
			throw new IllegalArgumentException("Not allowed to cancel another user's request");
		}
		if (req.getStatus() != PlanChangeRequestStatus.PENDING) {
			throw new IllegalArgumentException("Only PENDING requests can be cancelled");
		}
		req.setStatus(PlanChangeRequestStatus.CANCELLED);
		req.setReviewedAt(LocalDateTime.now());
		req.setReviewedBy(userId);
		PlanChangeRequest saved = repository.save(req);
		log.info("cancel - requestId={} userId={}", saved.getId(), userId);
		return PlanChangeRequestDto.from(saved);
	}

	// ── helpers ──────────────────────────────────────────────────────────────

	private PlanChangeRequest loadPendingForUpdate(Long requestId) {
		PlanChangeRequest req = repository.findByIdForUpdate(requestId)
			.orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));
		if (req.getStatus() != PlanChangeRequestStatus.PENDING) {
			throw new IllegalArgumentException("Request is already " + req.getStatus());
		}
		return req;
	}

	/**
	 * Best-effort approval notification on both channels. Wrapped in a
	 * try/catch per channel so a flaky Twilio / SMTP doesn't roll back the
	 * approval transaction — the plan is already applied and the row is
	 * APPROVED whatever happens here.
	 */
	private void notifyApproved(PlanChangeRequest req) {
		User user = userRepository.findById(req.getUserId()).orElse(null);
		if (user == null) {
			log.warn("notifyApproved - user not found, skipping (userId={})", req.getUserId());
			return;
		}
		final String plan = req.getRequestedPlan() == null ? "" : req.getRequestedPlan().name();
		final String subject = "Your KeyBricks " + plan + " plan is active";
		final String body =
			"Hi " + safeName(user) + ",\n\n" +
			"Your request for the " + plan + " plan has been approved and is now active. " +
			"Open the app to start using the new features.\n\n" +
			"— KeyBricks";
		safeEmail(user.getEmail(), body, subject);
		safeSms(user.getMobile(), "KeyBricks: Your " + plan + " plan is active. Open the app to use the new features.");
	}

	private void notifyRejected(PlanChangeRequest req) {
		User user = userRepository.findById(req.getUserId()).orElse(null);
		if (user == null) {
			log.warn("notifyRejected - user not found, skipping (userId={})", req.getUserId());
			return;
		}
		final String plan = req.getRequestedPlan() == null ? "" : req.getRequestedPlan().name();
		final String reasonLine = (req.getRejectionReason() != null && !req.getRejectionReason().isBlank())
				? "Reason: " + req.getRejectionReason() + "\n\n"
				: "";
		final String subject = "Update on your KeyBricks plan request";
		final String body =
			"Hi " + safeName(user) + ",\n\n" +
			"Your request for the " + plan + " plan was not approved.\n\n" +
			reasonLine +
			"Reach out to support if you'd like more details.\n\n" +
			"— KeyBricks";
		safeEmail(user.getEmail(), body, subject);
		safeSms(user.getMobile(), "KeyBricks: Your " + plan + " plan request was not approved.");
	}

	private void safeEmail(String to, String body, String subject) {
		if (to == null || to.isBlank()) return;
		try {
			commService.sendEmail(to, body, subject);
		} catch (Exception e) {
			log.warn("plan-change email failed to {}: {}", to, e.getMessage());
		}
	}

	private void safeSms(String mobile, String message) {
		if (mobile == null || mobile.isBlank()) return;
		try {
			commService.sendSMSMessage(mobile, message);
		} catch (Exception e) {
			log.warn("plan-change SMS failed to {}: {}", mobile, e.getMessage());
		}
	}

	private String safeName(User user) {
		String n = user.getName();
		return (n == null || n.isBlank()) ? "there" : n;
	}

	private String trim(String s) {
		if (s == null) return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
