package com.api.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;
import com.api.notifications.OtpService;
import com.api.prop.Property;
import com.api.prop.PropertyDto;
import com.api.prop.PropertyRepository;
import com.api.userproperty.UserPropertyRelation;
import com.api.userproperty.UserPropertyRelationDto;
import com.api.userproperty.UserPropertyRelationRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;
	private final UserPropertyRelationRepository propertyRelationRepository;
	private final OtpService otpService;
	private final ModelMapper mapper;

	public UserService(UserRepository userRepository, PropertyRepository propertyRepository,
			UserPropertyRelationRepository propertyRelationRepository, OtpService otpService, ModelMapper mapper) {
		this.userRepository = userRepository;
		this.propertyRepository = propertyRepository;
		this.propertyRelationRepository = propertyRelationRepository;
		this.otpService = otpService;
		this.mapper = mapper;
	}

	// ---------------- REGISTER ----------------
	public UserDto signup(UserDto userDto) {
		if (userDto.getEmail() == null && userDto.getMobile() == null) {
			throw new IllegalArgumentException("Email or mobile required");
		}

		if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
			throw new IllegalArgumentException("Email already in use");
		}

		if (userDto.getMobile() != null && userRepository.existsByMobile(userDto.getMobile())) {
			throw new IllegalArgumentException("Mobile already in use");
		}

		User user = mapper.map(userDto, User.class);
		if (user.getUserRole() == null) {
			user.setUserRole(MasterEnums.UserRoleEnum.CLIENT);
		}
		user.setUserStatus(MasterEnums.UserStatusEnum.PENDING);
		userRepository.save(user);

		// Generate OTP
		String otp = otpService.generateOtp();
		user.setOtp(otp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		// Send OTP
		if (user.getMobile() != null)
			otpService.sendOtpOnSms(user.getMobile(), otp);
		if (user.getEmail() != null)
			otpService.sendOtpOnEmail(user.getEmail(), otp);

		return mapper.map(user, UserDto.class);
	}

	public ResponseEntity<String> verifyOtp(String identifier, String otp) {
		Optional<User> userOptional = identifier.contains("@") ? userRepository.findByEmail(identifier)
				: userRepository.findByMobile(identifier);

		User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (!otp.equals(user.getOtp())) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		if (user.getOtpGeneratedAt() == null
				|| user.getOtpGeneratedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("OTP expired");
		}

		user.setUserStatus(MasterEnums.UserStatusEnum.ACTIVE);
		user.setIsVerified(true);
		userRepository.save(user);

		return ResponseEntity.ok("OTP verified! User activated.");
	}

	public ResponseEntity<String> resendOtp(String identifier) {
		Optional<User> userOptional = identifier.contains("@") ? userRepository.findByEmail(identifier)
				: userRepository.findByMobile(identifier);

		User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (Boolean.TRUE.equals(user.getIsVerified())) {
			return ResponseEntity.badRequest().body("User already verified");
		}

		if (user.getOtpGeneratedAt() != null && user.getOtpGeneratedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("Please wait 1 min before requesting a new OTP");
		}

		String newOtp = otpService.generateOtp();
		user.setOtp(newOtp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		if (user.getMobile() != null)
			otpService.sendOtpOnSms(user.getMobile(), newOtp);
		if (user.getEmail() != null)
			otpService.sendOtpOnEmail(user.getEmail(), newOtp);

		return ResponseEntity.ok("New OTP has been sent");
	}

	// ---------------- LOGIN ----------------
	public ResponseEntity<String> login(UserDto reqDto) {
		User user = null;

		if (reqDto.getEmail() != null) {
			user = userRepository.findByEmail(reqDto.getEmail())
					.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
		} else if (reqDto.getMobile() != null) {
			user = userRepository.findByMobile(reqDto.getMobile())
					.orElseThrow(() -> new IllegalArgumentException("Invalid mobile or password"));
		} else {
			return ResponseEntity.badRequest().body("Email or mobile required for login");
		}

		if (!reqDto.getPassword().equals(user.getPassword())) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}

		return ResponseEntity.ok("Login successful for user: " + user.getName());
	}

	// ---------------- PROFILE ----------------
	public UserDto getProfile(String emailOrMobile) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		return userOpt.map(user -> mapper.map(user, UserDto.class))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	public UserDto updateProfile(UserDto userDto) {
		Optional<User> userOpt = userDto.getEmail() != null ? userRepository.findByEmail(userDto.getEmail())
				: userRepository.findByMobile(userDto.getMobile());

		User updated = userOpt.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (userDto.getName() != null)
			updated.setName(userDto.getName());
		if (userDto.getAddress() != null)
			updated.setAddress(userDto.getAddress());
		if (userDto.getMobile() != null)
			updated.setMobile(userDto.getMobile());

		userRepository.save(updated);
		return mapper.map(updated, UserDto.class);
	}

	// ---------------- RESET PASSWORD ----------------
	public ResponseEntity<String> resetPassword(String emailOrMobile, String newPassword) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		User user = userOpt.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setPassword(newPassword);
		userRepository.save(user);

		return ResponseEntity.ok("Password reset successfully!");
	}

	// ---------------- PROPERTY RELATIONS ----------------
	public UserPropertyRelationDto markFavourite(Long userId, Long propertyId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		Property property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		// Toggle favourite status
		if (relation.isFavourite()) {
			relation.setFavourite(false);
			relation.setFavouriteDate(null);
		} else {
			relation.setFavourite(true);
			relation.setFavouriteDate(LocalDateTime.now());
		}

		UserPropertyRelation savedRelation = propertyRelationRepository.save(relation);

		// Convert entity to DTO
		return mapper.map(savedRelation, UserPropertyRelationDto.class);
	}

	public UserPropertyRelationDto markInterested(Long userId, Long propertyId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		Property property = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		// Toggle inquiry status
		if (relation.isInquiry()) {
			relation.setInquiry(false);
			relation.setInquiryDate(null);
		} else {
			relation.setInquiry(true);
			relation.setInquiryDate(LocalDateTime.now());
		}

		UserPropertyRelation savedRelation = propertyRelationRepository.save(relation);

		// Convert entity to DTO
		return mapper.map(savedRelation, UserPropertyRelationDto.class);
	}

	public List<PropertyDto> getFavouriteProperties(Long userId) {
		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isFavourite) // filter
																													// favorites
				.map(UserPropertyRelation::getProperty) // get Property entity
				.map(property -> mapper.map(property, PropertyDto.class)) // map to DTO
				.collect(Collectors.toList());
	}

	public List<PropertyDto> getInquiredProperties(Long userId) {
		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isInquiry)
				.map(UserPropertyRelation::getProperty).map(property -> mapper.map(property, PropertyDto.class)) // map
																													// entity
																													// to
																													// DTO
				.collect(Collectors.toList());
	}

	// ---------------- LOGOUT ----------------
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logged out successfully!");
	}
}
