package com.api.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.notifications.CommService;
import com.api.notifications.NotificationService;
import com.api.prop.Property;
import com.api.prop.PropertyDto;
import com.api.prop.PropertyRepository;
import com.api.userproperty.UserPropertyRelation;
import com.api.userproperty.UserPropertyRelationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;
	private final UserPropertyRelationRepository propertyRelationRepository;
	private final CommService commService;
	private final NotificationService notificationService;
	private final ModelMapper mapper;

	public UserService(UserRepository userRepository, PropertyRepository propertyRepository,
			UserPropertyRelationRepository propertyRelationRepository, CommService commService,
			NotificationService notificationService, ModelMapper mapper) {
		this.userRepository = userRepository;
		this.propertyRepository = propertyRepository;
		this.propertyRelationRepository = propertyRelationRepository;
		this.commService = commService;
		this.notificationService = notificationService;
		this.mapper = mapper;
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
		user.setUserStatus(MasterEnums.UserStatusEnum.ACTIVE);
		userRepository.save(user);

		String otp = commService.generateOtp();
		user.setOtp(otp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		try {
			if (user.getMobile() != null) {
				log.debug("signup - Sending OTP via SMS to {}", user.getMobile());
				commService.sendSMSMessage(user.getMobile(), "One Time Password is: " + otp + "\nValid for 10 minutes");
			}
			if (user.getEmail() != null) {
				log.debug("signup - Sending OTP via email to {}", user.getEmail());
				commService.sendEmail(user.getEmail(), "One Time Password is: " + otp + "\nValid for 10 minutes",
						"OTP for User SignUp");
			}
		} catch (Exception e) {
			log.error("signup - Failed to send OTP for identifier={}: {}", identifier, e.getMessage(), e);
		}
		log.info("signup - User registered successfully with id={}", user.getId());
		return mapper.map(user, UserDto.class);
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
		notificationService.notifyWelcome(user);
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

		try {
			if (identifier.contains("@")) {
				log.debug("resendOtp - Sending new OTP via SMS to {}", user.getMobile());
				commService.sendSMSMessage(user.getMobile(), "Your OTP is: " + newOtp + "\nValid for 10 minutes");
			} else {
				log.debug("resendOtp - Sending new OTP via email to {}", user.getEmail());
				commService.sendEmail(user.getEmail(), "Your OTP is: " + newOtp + "\nValid for 10 minutes",
						"OTP for User SignUp");
			}
		} catch (Exception e) {
			log.error("resendOtp - Failed to send OTP for identifier={}: {}", identifier, e.getMessage(), e);
		}
		log.info("resendOtp - New OTP dispatched for identifier={}", identifier);
		return ResponseEntity.ok("New OTP has been sent");
	}

	// ---------------- LOGIN ----------------
	public ResponseEntity<UserDto> login(UserDto reqDto) {
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

		if (!reqDto.getPassword().equals(user.getPassword())) {
			log.warn("login - Invalid password for identifier={}", identifier);
			return ResponseEntity.status(401).build();
		}

		log.info("login - Successful login for userId={}", user.getId());
		UserDto responseDto = mapper.map(user, UserDto.class);
		return ResponseEntity.ok(responseDto);
	}

	// ---------------- PROFILE ----------------
	public UserDto getProfile(String emailOrMobile) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		return userOpt.map(user -> mapper.map(user, UserDto.class))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	public UserDto getProfilebyUserId(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);

		return userOpt.map(user -> mapper.map(user, UserDto.class))
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
		return mapper.map(updated, UserDto.class);
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
		user.setPassword(userDto.getPassword());
		userRepository.save(user);
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

		return relations.stream().map(upr -> {
			Property p = upr.getProperty();
			PropertyDto dto = new PropertyDto();

			// --- Basic Info ---
			dto.setId(p.getId());
			dto.setTitle(p.getTitle());
			dto.setAddress(p.getAddress());
			dto.setCity(p.getCity());
			dto.setState(p.getState());
			dto.setType(p.getType());
			dto.setCategory(p.getCategory());
			dto.setRentOrSale(p.getRentOrSale());
			dto.setPropertyStatus(p.getPropertyStatus());
			dto.setVerified(p.isVerified());

			// --- Pricing ---
			dto.setPrice(p.getPrice());
			dto.setCurrency(p.getCurrency());
			dto.setMonthlyRent(p.getMonthlyRent());
			dto.setSecurityDeposit(p.getSecurityDeposit());
			dto.setBrokerage(p.getBrokerage());
			dto.setNegotiable(p.getNegotiable());
			dto.setLoanAvailable(p.getLoanAvailable());

			// --- Area & Rooms ---
			dto.setBedrooms(p.getBedrooms());
			dto.setBathrooms(p.getBathrooms());
			dto.setCarpetArea(p.getCarpetArea());
			dto.setSuperArea(p.getSuperArea());

			// --- Location ---
			dto.setLocation(p.getLocation());
			dto.setLandmark(p.getLandmark());
			dto.setLatitude(p.getLatitude());
			dto.setLongitude(p.getLongitude());
			dto.setFacing(p.getFacing());

			// --- Building Info ---
			dto.setFloorNumber(p.getFloorNumber());
			dto.setTotalFloors(p.getTotalFloors());
			dto.setParkingCount(p.getParkingCount());
			dto.setParkingType(p.getParkingType());
			dto.setPropertyAge(p.getPropertyAge());
			dto.setOwnershipType(p.getOwnershipType());
			dto.setFurnishing(p.getFurnishing());
			dto.setConstructionStatus(p.getConstructionStatus());
			dto.setReadyDate(p.getReadyDate());

			// --- Project / Builder ---
			dto.setProjectName(p.getProjectName());
			dto.setBuilderName(p.getBuilderName());
			dto.setReraApproved(p.getReraApproved());
			dto.setReraNumber(p.getReraNumber());

			// --- Tenant Rules (for Rent) ---
			dto.setPreferredTenants(p.getPreferredTenants());
			dto.setPetsAllowed(p.getPetsAllowed());
			dto.setNonVegAllowed(p.getNonVegAllowed());
			dto.setLeaseDuration(p.getLeaseDuration());
			dto.setNoticePeriod(p.getNoticePeriod());
			dto.setMaintenanceIncluded(p.getMaintenanceIncluded());

			// --- Meta ---
			dto.setPostedBy(p.getPostedBy());
			dto.setPostedByUser(p.getPostedByUser());
			dto.setPostDate(p.getPostDate());
			dto.setContactNumber(p.getContactNumber());
			dto.setDescription(p.getDescription());

			// --- Stats ---
			dto.setViewsCount(p.getViewsCount());
			dto.setShortListCount(p.getShortListCount());

			// --- Amenities ---
			dto.setAmenitiesFromList(p.getAmenitiesAsList());

			// --- Audit ---
			dto.setCode(p.getCode());
			dto.setLastUpdatedTs(p.getLastUpdatedTs());
			dto.setUpdatedBy(p.getUpdatedBy());

			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	public List<PropertyDto> getInquiredProperties(Long userId) {
		List<UserPropertyRelation> relations = propertyRelationRepository.findByUserIdAndInquiryTrue(userId);

		return relations.stream().map(upr -> {
			Property p = upr.getProperty();
			PropertyDto dto = new PropertyDto();

			// --- Basic Info ---
			dto.setId(p.getId());
			dto.setTitle(p.getTitle());
			dto.setAddress(p.getAddress());
			dto.setCity(p.getCity());
			dto.setState(p.getState());
			dto.setType(p.getType());
			dto.setCategory(p.getCategory());
			dto.setRentOrSale(p.getRentOrSale());
			dto.setPropertyStatus(p.getPropertyStatus());
			dto.setVerified(p.isVerified());

			// --- Pricing ---
			dto.setPrice(p.getPrice());
			dto.setCurrency(p.getCurrency());
			dto.setMonthlyRent(p.getMonthlyRent());
			dto.setSecurityDeposit(p.getSecurityDeposit());
			dto.setBrokerage(p.getBrokerage());
			dto.setNegotiable(p.getNegotiable());
			dto.setLoanAvailable(p.getLoanAvailable());

			// --- Area & Rooms ---
			dto.setBedrooms(p.getBedrooms());
			dto.setBathrooms(p.getBathrooms());
			dto.setCarpetArea(p.getCarpetArea());
			dto.setSuperArea(p.getSuperArea());

			// --- Location ---
			dto.setLocation(p.getLocation());
			dto.setLandmark(p.getLandmark());
			dto.setLatitude(p.getLatitude());
			dto.setLongitude(p.getLongitude());
			dto.setFacing(p.getFacing());

			// --- Building Info ---
			dto.setFloorNumber(p.getFloorNumber());
			dto.setTotalFloors(p.getTotalFloors());
			dto.setParkingCount(p.getParkingCount());
			dto.setParkingType(p.getParkingType());
			dto.setPropertyAge(p.getPropertyAge());
			dto.setOwnershipType(p.getOwnershipType());
			dto.setFurnishing(p.getFurnishing());
			dto.setConstructionStatus(p.getConstructionStatus());
			dto.setReadyDate(p.getReadyDate());

			// --- Project / Builder ---
			dto.setProjectName(p.getProjectName());
			dto.setBuilderName(p.getBuilderName());
			dto.setReraApproved(p.getReraApproved());
			dto.setReraNumber(p.getReraNumber());

			// --- Tenant Rules (for Rent) ---
			dto.setPreferredTenants(p.getPreferredTenants());
			dto.setPetsAllowed(p.getPetsAllowed());
			dto.setNonVegAllowed(p.getNonVegAllowed());
			dto.setLeaseDuration(p.getLeaseDuration());
			dto.setNoticePeriod(p.getNoticePeriod());
			dto.setMaintenanceIncluded(p.getMaintenanceIncluded());

			// --- Meta ---
			dto.setPostedBy(p.getPostedBy());
			dto.setPostedByUser(p.getPostedByUser());
			dto.setPostDate(p.getPostDate());
			dto.setContactNumber(p.getContactNumber());
			dto.setDescription(p.getDescription());

			// --- Stats ---
			dto.setViewsCount(p.getViewsCount());
			dto.setShortListCount(p.getShortListCount());

			// --- Amenities ---
			dto.setAmenitiesFromList(p.getAmenitiesAsList());

			// --- Audit ---
			dto.setCode(p.getCode());
			dto.setLastUpdatedTs(p.getLastUpdatedTs());
			dto.setUpdatedBy(p.getUpdatedBy());

			return dto;
		}).collect(Collectors.toList());
	}

	// ---------------- LOGOUT ----------------
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logged out successfully!");
	}
}
