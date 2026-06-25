package com.api.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import com.api.enums.MasterEnums;
import com.api.notifications.CommService;
import com.api.notifications.NotificationService;
import com.api.notifications.events.OtpGeneratedEvent;
import com.api.notifications.events.PasswordResetEvent;
import com.api.notifications.events.UserRegisteredEvent;
import com.api.plan.PlanService;
import com.api.prop.Property;
import com.api.prop.PropertyDto;
import com.api.prop.PropertyRepository;
import com.api.prop.PropertyService;
import com.api.security.JwtUtil;
import com.api.userproperty.UserPropertyRelation;
import com.api.userproperty.UserPropertyRelationRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;
	private final PropertyService propertyService;
	private final UserPropertyRelationRepository propertyRelationRepository;
	private final CommService commService;
	private final NotificationService notificationService;
	private final ApplicationEventPublisher eventPublisher;
	private final ModelMapper mapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final PlanService planService;


	public UserService(UserRepository userRepository, PropertyRepository propertyRepository,
			PropertyService propertyService,
			UserPropertyRelationRepository propertyRelationRepository, CommService commService,
			NotificationService notificationService, ApplicationEventPublisher eventPublisher,
			ModelMapper mapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, PlanService planService) {
		this.userRepository = userRepository;
		this.propertyRepository = propertyRepository;
		this.propertyService = propertyService;
		this.propertyRelationRepository = propertyRelationRepository;
		this.commService = commService;
		this.notificationService = notificationService;
		this.eventPublisher = eventPublisher;
		this.mapper = mapper;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.planService = planService;
	}

	/**
	 * Decorates a {@link UserDto} with the plan price and feature flag map
	 * derived from {@link User#getUserPackage()}. Called on every outbound
	 * profile/login response so the client can gate its UI in one round-trip.
	 */
	private UserDto withPlanInfo(UserDto dto, User user) {
		// effectivePackage() lazily downgrades expired subscriptions to BASIC
		// so the client always sees the tier the user is *currently entitled
		// to*, even if userPackage hasn't been touched after expiry.
		MasterEnums.PackageEnum pkg = user.effectivePackage();
		dto.setPlanPriceYearly(planService.getPriceYearly(pkg));
		dto.setPlanPropertyLimit(planService.getPropertyLimit(pkg));
		dto.setFeatureFlags(planService.getFeatureFlags(pkg));
		// Surface the effective plan name on the wire too (in case it
		// differs from userPackage due to expiry).
		dto.setUserPackage(pkg);
		return dto;
	}

	// ---------------- REGISTER ----------------

	
	public UserDto signup(UserDto userDto) {
		String identifier = userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile();
		log.info("signup - Signup attempt for identifier={}", identifier);

		if (userDto.getEmail() == null && userDto.getMobile() == null) {
			log.warn("signup - Rejected: no email or mobile provided");
			throw new IllegalArgumentException("Email or mobile required");
		}

		if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
			log.warn("signup - Rejected: email already in use [{}]", userDto.getEmail());
			throw new IllegalArgumentException("Email already in use");
		}

		if (userDto.getMobile() != null && userRepository.existsByMobile(userDto.getMobile())) {
			log.warn("signup - Rejected: mobile already in use [{}]", userDto.getMobile());
			throw new IllegalArgumentException("Mobile already in use");
		}

		User user = mapper.map(userDto, User.class);
		if (user.getUserRole() == null) {
			user.setUserRole(MasterEnums.UserRoleEnum.CLIENT);
		}
		// Every new signup starts on the free BASIC plan.
		if (user.getUserPackage() == null) {
			user.setUserPackage(MasterEnums.PackageEnum.BASIC);
		}
		user.setUserStatus(MasterEnums.UserStatusEnum.ACTIVE);
		if (user.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		String otp = commService.generateOtp();
		user.setOtp(otp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		// Publish event — NotificationEventListener sends HTML OTP email + SMS asynchronously
		eventPublisher.publishEvent(new OtpGeneratedEvent(
				user.getName(), user.getEmail(), user.getMobile(), otp, "SIGNUP"));
		log.info("signup - User registered successfully with id={}", user.getId());
		return withPlanInfo(mapper.map(user, UserDto.class), user);
	}

	
	public ResponseEntity<String> verifyOtp(String identifier, String otp) {
		log.info("verifyOtp - OTP verification for identifier={}", identifier);
		Optional<User> userOptional = identifier.contains("@") ? userRepository.findByEmail(identifier)
				: userRepository.findByMobile(identifier);

		User user = userOptional.orElseThrow(() -> {
			log.warn("verifyOtp - User not found for identifier={}", identifier);
			return new IllegalArgumentException("User not found");
		});

		if (!otp.equals(user.getOtp())) {
			log.warn("verifyOtp - Invalid OTP for identifier={}", identifier);
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		if (user.getOtpGeneratedAt() == null
				|| user.getOtpGeneratedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
			log.warn("verifyOtp - OTP expired for identifier={}", identifier);
			return ResponseEntity.badRequest().body("OTP expired");
		}

		user.setUserStatus(MasterEnums.UserStatusEnum.ACTIVE);
		user.setIsVerified(true);
		userRepository.save(user);
		// Publish event — sends HTML welcome email + SMS asynchronously with tracking
		eventPublisher.publishEvent(new UserRegisteredEvent(
				user.getId(), user.getName(), user.getEmail(), user.getMobile()));
		log.info("verifyOtp - User verified successfully, id={}", user.getId());
		return ResponseEntity.ok("OTP verified! User activated.");
	}

	
	public ResponseEntity<String> resendOtp(String identifier) {
		log.info("resendOtp - Resend OTP request for identifier={}", identifier);
		Optional<User> userOptional = identifier.contains("@") ? userRepository.findByEmail(identifier)
				: userRepository.findByMobile(identifier);

		User user = userOptional.orElseThrow(() -> {
			log.warn("resendOtp - User not found for identifier={}", identifier);
			return new IllegalArgumentException("User not found");
		});

		if (user.getOtpGeneratedAt() != null && user.getOtpGeneratedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
			log.warn("resendOtp - Rate-limited: OTP requested too soon for identifier={}", identifier);
			return ResponseEntity.badRequest().body("Please wait 1 min before requesting a new OTP");
		}

		String newOtp = commService.generateOtp();
		user.setOtp(newOtp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		// Publish event — NotificationEventListener sends HTML OTP email + SMS asynchronously
		eventPublisher.publishEvent(new OtpGeneratedEvent(
				user.getName(), user.getEmail(), user.getMobile(), newOtp, "RESEND"));
		log.info("resendOtp - New OTP dispatched for identifier={}", identifier);
		return ResponseEntity.ok("New OTP has been sent");
	}

	// ---------------- LOGIN ----------------

	
	public ResponseEntity<LoginResponse> login(UserDto reqDto) {
		String identifier = reqDto.getEmail() != null ? reqDto.getEmail() : reqDto.getMobile();
		log.info("login - Login attempt for identifier={}", identifier);
		User user;

		if (reqDto.getEmail() != null) {
			user = userRepository.findByEmail(reqDto.getEmail())
					.orElseThrow(() -> {
						log.warn("login - User not found for email={}", reqDto.getEmail());
						return new IllegalArgumentException("Invalid email or password");
					});
		} else if (reqDto.getMobile() != null) {
			user = userRepository.findByMobile(reqDto.getMobile())
					.orElseThrow(() -> {
						log.warn("login - User not found for mobile={}", reqDto.getMobile());
						return new IllegalArgumentException("Invalid mobile or password");
					});
		} else {
			log.warn("login - Rejected: no email or mobile provided");
			return ResponseEntity.badRequest().build();
		}

		if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
			// Fallback: plain-text password stored by legacy data — compare directly and upgrade on match
			if (!reqDto.getPassword().equals(user.getPassword())) {
				log.warn("login - Invalid password for identifier={}", identifier);
				return ResponseEntity.status(401).build();
			}
			user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
			userRepository.save(user);
			log.info("login - Upgraded plain-text password to BCrypt for userId={}", user.getId());
		}

		String role = user.getUserRole() != null ? user.getUserRole().name() : "CLIENT";
		String token = jwtUtil.generateToken(user.getId(), identifier, role);
		UserDto responseDto = withPlanInfo(mapper.map(user, UserDto.class), user);
		log.info("login - Successful login for userId={}", user.getId());
		return ResponseEntity.ok(new LoginResponse(token, responseDto));
	}

	// ---------------- PROFILE ----------------

	
	public UserDto getProfile(String emailOrMobile) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		return userOpt.map(user -> withPlanInfo(mapper.map(user, UserDto.class), user))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}


	public UserDto getProfilebyUserId(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);

		return userOpt.map(user -> withPlanInfo(mapper.map(user, UserDto.class), user))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	
	public UserDto updateProfile(UserDto userDto) {
		Optional<User> userOpt = userRepository.findById(userDto.getId());

		User updated = userOpt.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (userDto.getName() != null)
			updated.setName(userDto.getName());
		if (userDto.getAddress() != null)
			updated.setAddress(userDto.getAddress());
		if (userDto.getMobile() != null)
			updated.setMobile(userDto.getMobile());
		if (userDto.getUserPackage() != null)
			updated.setUserPackage(userDto.getUserPackage());

		if (userDto.getEmail() != null)
			updated.setEmail(userDto.getEmail());

		userRepository.save(updated);
		return withPlanInfo(mapper.map(updated, UserDto.class), updated);
	}

	// ---------------- RESET PASSWORD ----------------

	
	public ResponseEntity<String> resetPassword(UserDto userDto) {
		String identifier = userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile();
		log.info("resetPassword - Password reset request for identifier={}", identifier);
		Optional<User> userOpt = null;
		if (userDto.getEmail() != null)
			userOpt = userRepository.findByEmail(userDto.getEmail());
		else if (userDto.getMobile() != null)
			userOpt = userRepository.findByMobile(userDto.getMobile());
		if (userOpt == null) {
			log.warn("resetPassword - User not found for identifier={}", identifier);
			throw new IllegalArgumentException("User not found");
		}
		User user = userOpt.orElseThrow(() -> {
			log.warn("resetPassword - User not found for identifier={}", identifier);
			return new IllegalArgumentException("User not found");
		});
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userRepository.save(user);
		// Notify user of successful password reset
		eventPublisher.publishEvent(new PasswordResetEvent(
				user.getName(), user.getEmail(), user.getMobile()));
		log.info("resetPassword - Password reset successfully for userId={}", user.getId());
		return ResponseEntity.ok("Password reset successfully!");
	}

	// ---------------- PROPERTY RELATIONS ----------------

	
	@Transactional
	public Long markFavourite(Long userId, Long propertyId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		Property property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					newRelation.setFavourite(false); // default
					return newRelation;
				});

		boolean isFav = Boolean.TRUE.equals(relation.getFavourite());

		relation.setFavourite(!isFav);
		relation.setFavouriteDate(!isFav ? LocalDateTime.now() : null);

		UserPropertyRelation savedRelation = propertyRelationRepository.save(relation);

		return propertyId;
	}

	
	@Transactional
	public Long markInterested(Long userId, Long propertyId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		Property property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					newRelation.setInquiry(false); // default
					return newRelation;
				});

		boolean isInterested = Boolean.TRUE.equals(relation.getInquiry());

		relation.setInquiry(!isInterested);
		relation.setInquiryDate(!isInterested ? LocalDateTime.now() : null);

		UserPropertyRelation savedRelation = propertyRelationRepository.save(relation);

		return propertyId;
	}

	
	@Transactional
	public List<PropertyDto> getFavouriteProperties(Long userId) {
		List<UserPropertyRelation> relations = propertyRelationRepository.findByUserIdAndFavouriteTrue(userId);
		return batchLoadPropertyDtos(relations);
	}

	@Transactional
	public List<PropertyDto> getInquiredProperties(Long userId) {
		List<UserPropertyRelation> relations = propertyRelationRepository.findByUserIdAndInquiryTrue(userId);
		return batchLoadPropertyDtos(relations);
	}

	// Fetches all properties for the given relations in one IN query, then maps via PropertyService.toDto().
	// Avoids N+1: previously each upr.getProperty() triggered a separate SELECT.
	private List<PropertyDto> batchLoadPropertyDtos(List<UserPropertyRelation> relations) {
		if (relations.isEmpty()) return List.of();
		List<Long> ids = relations.stream()
				.map(r -> r.getProperty().getId())
				.collect(Collectors.toList());
		Map<Long, Property> propertyMap = propertyRepository.findAllById(ids).stream()
				.collect(Collectors.toMap(Property::getId, p -> p));
		return ids.stream()
				.map(propertyMap::get)
				.filter(p -> p != null)
				.map(propertyService::toDto)
				.collect(Collectors.toList());
	}

	// ---------------- PLAN CHANGE (ADMIN) ----------------

	/**
	 * Assigns or extends a paid subscription for {@code userId}. BASIC clears
	 * the subscription window (no expiry tracking on the free tier).
	 *
	 * <p>Called by the admin-only {@code POST /api/user/{userId}/plan}
	 * endpoint. The reason argument is logged so we have a paper trail
	 * until a proper audit table lands.</p>
	 */
	@Transactional
	public UserDto changePlan(Long userId, MasterEnums.PackageEnum plan, Integer durationYears, String reason) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

		MasterEnums.PackageEnum before = user.getUserPackage();
		LocalDateTime now = LocalDateTime.now();
		int years = (durationYears == null || durationYears <= 0) ? 1 : durationYears;

		user.setUserPackage(plan);
		if (plan == MasterEnums.PackageEnum.BASIC) {
			user.setSubscriptionStartAt(null);
			user.setSubscriptionEndAt(null);
		} else {
			user.setSubscriptionStartAt(now);
			user.setSubscriptionEndAt(now.plusYears(years));
		}
		userRepository.save(user);

		log.info("changePlan - userId={} {} -> {} (ends={}, reason='{}')",
				userId, before, plan, user.getSubscriptionEndAt(), reason);
		return withPlanInfo(mapper.map(user, UserDto.class), user);
	}

	// ---------------- LOGOUT ----------------

	
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logged out successfully!");
	}
}
