package com.api.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;
import com.api.enums.MasterEnums.UserStatusEnum;
import com.api.notifications.OtpService;
import com.api.prop.Property;
import com.api.prop.PropertyRepository;
import com.api.userproperty.UserPropertyRelation;
import com.api.userproperty.UserPropertyRelationRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private UserPropertyRelationRepository propertyRelationRepository;

	@Autowired
	private OtpService otpService;

	// ---------------- REGISTER ----------------

	public ResponseEntity<?> signup(User user) {

		if (user.getEmail() == null && user.getMobile() == null) {
			return ResponseEntity.badRequest().body("Email or mobile required");
		}

		if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email already in use");
		}

		if (user.getMobile() != null && userRepository.existsByMobile(user.getMobile())) {
			return ResponseEntity.badRequest().body("Mobile already in use");
		}

		// Save user
		if (user.getUserRole() == null) {
			user.setUserRole(MasterEnums.UserRoleEnum.CLIENT);
		}
		user.setUserStatus(UserStatusEnum.PENDING);
		userRepository.save(user);

		// Generate OTP
		String otp = otpService.generateOtp();

		// Store OTP in user table or separate otp table
		user.setOtp(otp);
		user.setOtpGeneratedAt(LocalDateTime.now());
		userRepository.save(user);

		// Send OTP
		if (user.getMobile() != null) {
			otpService.sendOtpOnSms(user.getMobile(), otp);
		}
		if (user.getEmail() != null) {
			otpService.sendOtpOnEmail(user.getEmail(), otp);
		}

		return ResponseEntity.ok(user);
	}

	public ResponseEntity<String> verifyOtp(String identifier, String otp) {

		// identifier = email or mobile
		Optional<User> userOptional = null;

		if (identifier.contains("@")) {
			userOptional = userRepository.findByEmail(identifier);
		} else {
			userOptional = userRepository.findByMobile(identifier);
		}

		// User not found
		if (userOptional.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found");
		}

		User user = userOptional.get();
		if (user == null) {
			return ResponseEntity.badRequest().body("User not found");
		}

		// OTP match
		if (!otp.equals(user.getOtp())) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		// Check expiry (10 minutes)
		if (user.getOtpGeneratedAt() == null
				|| user.getOtpGeneratedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("OTP expired");
		}

		// Mark user as verified
		user.setUserStatus(UserStatusEnum.ACTIVE);
		user.setIsVerified(true);
		userRepository.save(user);

		return ResponseEntity.ok("OTP verified! User activated.");
	}
	public ResponseEntity<String> resendOtp(String identifier) {

        Optional<User> userOptional;

        // Identify: email or mobile
        if (identifier.contains("@")) {
            userOptional = userRepository.findByEmail(identifier);
        } else {
            userOptional = userRepository.findByMobile(identifier);
        }

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        // Already verified
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            return ResponseEntity.badRequest().body("User already verified");
        }

        // Rate limit: allow resend only after 60 seconds
        if (user.getOtpGeneratedAt() != null &&
                user.getOtpGeneratedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Please wait 1 min before requesting a new OTP");
        }

        // Generate new OTP
        String newOtp = otpService.generateOtp();
        user.setOtp(newOtp);
        user.setOtpGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send OTP via SMS or Email
        if (user.getMobile() != null) {
            otpService.sendOtpOnSms(user.getMobile(), newOtp);
        }

        if (user.getEmail() != null) {
            otpService.sendOtpOnEmail(user.getEmail(), newOtp);
        }

        return ResponseEntity.ok("New OTP has been sent");
    }
	// ---------------- LOGIN ----------------
	public ResponseEntity<String> login(User req) {
		try {
			User user = null;

			if (req.getEmail() != null && req.getEmail().contains("@")) {
				user = userRepository.findByEmail(req.getEmail())
						.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
			} else if (req.getMobile() != null) {
				user = userRepository.findByMobile(req.getMobile())
						.orElseThrow(() -> new IllegalArgumentException("Invalid mobile or password"));
			} else {
				return ResponseEntity.badRequest().body("Email or mobile required for login");
			}

			if (!req.getPassword().equalsIgnoreCase(user.getPassword())) {
				return ResponseEntity.status(401).body("Invalid credentials");
			}

			return ResponseEntity.ok("Login successful for user: " + user.getName());

		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Login failed: " + ex.getMessage());
		}
	}

	// ---------------- LOGOUT ----------------
	// Since there's no JWT, logout is just client-side
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logged out successfully!");
	}

	// ---------------- PROFILE ----------------
	public Optional<User> getProfile(String emailOrMobile) {
		if (emailOrMobile.contains("@"))
			return userRepository.findByEmail(emailOrMobile);
		else
			return userRepository.findByMobile(emailOrMobile);
	}

	public ResponseEntity<String> updateProfile(User updatedUser) {
		Optional<User> userOpt = updatedUser.getEmail() != null ? userRepository.findByEmail(updatedUser.getEmail())
				: userRepository.findByMobile(updatedUser.getMobile());

		if (userOpt.isPresent()) {
			User existing = userOpt.get();

			if (updatedUser.getName() != null)
				existing.setName(updatedUser.getName());
			if (updatedUser.getAddress() != null)
				existing.setAddress(updatedUser.getAddress());
			if (updatedUser.getMobile() != null)
				existing.setMobile(updatedUser.getMobile());

			userRepository.save(existing);
			return ResponseEntity.ok("Profile updated successfully!");
		}
		return ResponseEntity.badRequest().body("User not found!");
	}

	// ---------------- RESET PASSWORD ----------------
	public ResponseEntity<String> resetPassword(String emailOrMobile, String newPassword) {
		Optional<User> userOpt = emailOrMobile.contains("@") ? userRepository.findByEmail(emailOrMobile)
				: userRepository.findByMobile(emailOrMobile);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setPassword(newPassword);
			userRepository.save(user);
			return ResponseEntity.ok("Password reset successfully!");
		}
		return ResponseEntity.badRequest().body("User not found!");
	}

	// ---------------- PROPERTY RELATIONS ----------------
	public UserPropertyRelation markFavourite(Long userId, Long propertyId) {

		User user = userRepository.findById(userId).orElseThrow();
		Property property = propertyRepository.findById(propertyId).orElseThrow();

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		/*
		 * UserPropertyRelation relation =
		 * propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
		 * .orElseThrow(() -> new ResourceNotFoundException(
		 * "Relation not found for userId: " + userId + " and propertyId: " +
		 * propertyId));
		 */

		if (relation.isFavourite()) {
			relation.setFavourite(false);
			relation.setFavouriteDate(null);
		} else {
			relation.setFavourite(true);
			relation.setFavouriteDate(LocalDateTime.now());
		}

		return propertyRelationRepository.save(relation);

	}

	public UserPropertyRelation markInterested(Long userId, Long propertyId) {

		User user = userRepository.findById(userId).orElseThrow();
		Property property = propertyRepository.findById(propertyId).orElseThrow();

		UserPropertyRelation relation = propertyRelationRepository.findByUserIdAndPropertyId(userId, propertyId)
				.orElseGet(() -> {
					UserPropertyRelation newRelation = new UserPropertyRelation();
					newRelation.setUser(user);
					newRelation.setProperty(property);
					return newRelation;
				});

		if (relation.isInquiry()) {
			relation.setInquiry(false);
			relation.setInquiryDate(null);
		} else {
			relation.setInquiry(true);
			relation.setInquiryDate(LocalDateTime.now());
		}

		return propertyRelationRepository.save(relation);

	}

	public List<Property> getFavouriteProperties(Long userId) {

		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isFavourite)
				.map(UserPropertyRelation::getProperty) // extract property
				.toList();

	}

	public List<Property> getInquiredProperties(Long userId) {

		return propertyRelationRepository.findByUserId(userId).stream().filter(UserPropertyRelation::isInquiry)
				.map(UserPropertyRelation::getProperty) // extract property
				.toList();

	}

}
