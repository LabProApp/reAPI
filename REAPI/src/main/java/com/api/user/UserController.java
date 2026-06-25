package com.api.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.prop.PropertyDto;
import com.api.security.AuthUtils;
import com.api.userproperty.UserPropertyRelationDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs", description = "Operations related to User management")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Register a new user account")
	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {
		log.info("POST /api/user/signup - Signup attempt for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		UserDto savedUser = userService.signup(userDto);
		log.info("POST /api/user/signup - User created with id={}", savedUser.getId());
		return ResponseEntity.ok(savedUser);
	}

	@Operation(summary = "Verify OTP to activate account")
	@PostMapping("/verifyOtp")
	public ResponseEntity<String> verifyOtp(@RequestParam String identifier, @RequestParam String otp) {
		log.info("POST /api/user/verifyOtp - OTP verification request for identifier={}", identifier);
		ResponseEntity<String> response = userService.verifyOtp(identifier, otp);
		log.info("POST /api/user/verifyOtp - OTP verification result for identifier={}, status={}",
				identifier, response.getStatusCode());
		return response;
	}

	@Operation(summary = "Resend OTP to user")
	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestParam String identifier) {
		log.info("POST /api/user/resend-otp - Resend OTP request for identifier={}", identifier);
		ResponseEntity<String> response = userService.resendOtp(identifier);
		log.info("POST /api/user/resend-otp - Resend OTP result for identifier={}, status={}",
				identifier, response.getStatusCode());
		return response;
	}

	@Operation(summary = "Authenticate user and return JWT token")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserDto userDto) {
		log.info("POST /api/user/login - Login attempt for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		ResponseEntity<LoginResponse> response = userService.login(userDto);
		log.info("POST /api/user/login - Login result status={}", response.getStatusCode());
		return response;
	}

	@Operation(summary = "Log out the current user")
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		log.info("POST /api/user/logout - Logout request");
		return userService.logout();
	}

	@Operation(summary = "Reset user password")
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody UserDto userDto) {
		log.info("POST /api/user/reset-password - Password reset request for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		ResponseEntity<String> response = userService.resetPassword(userDto);
		log.info("POST /api/user/reset-password - Password reset completed, status={}", response.getStatusCode());
		return response;
	}

	@Operation(summary = "Get user profile by email or mobile")
	@GetMapping("/me")
	public ResponseEntity<UserDto> getProfile(@RequestParam String identifier) {
		log.info("GET /api/user/me - Fetching profile for identifier={}", identifier);
		UserDto userDto = userService.getProfile(identifier);
		log.info("GET /api/user/me - Profile fetched for userId={}", userDto.getId());
		return ResponseEntity.ok(userDto);
	}

	@Operation(summary = "Get user profile by user ID")
	@GetMapping("/profile/{userId}")
	public ResponseEntity<UserDto> getProfilebyUserId(@PathVariable Long userId) {
		log.info("GET /api/user/profile/{} - Fetching profile", userId);
		UserDto userDto = userService.getProfilebyUserId(userId);
		log.info("GET /api/user/profile/{} - Profile fetched", userId);
		return ResponseEntity.ok(userDto);
	}

	@Operation(summary = "Update user profile")
	@PutMapping("/update")
	public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
		log.info("PUT /api/user/update - Updating profile for userId={}", userDto.getId());
		UserDto updatedUser = userService.updateProfile(userDto);
		log.info("PUT /api/user/update - Profile updated for userId={}", updatedUser.getId());
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "ADMIN: assign or extend a user's paid subscription")
	@PostMapping("/{userId}/plan")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDto> changePlan(@PathVariable Long userId,
			@RequestBody AdminPlanAssignment body) {
		log.info("POST /api/user/{}/plan - plan={} years={} reason='{}'",
				userId, body.getPlan(), body.getDurationYears(), body.getReason());
		if (body.getPlan() == null) {
			return ResponseEntity.badRequest().build();
		}
		UserDto updated = userService.changePlan(userId, body.getPlan(),
				body.getDurationYears(), body.getReason());
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "Toggle property favourite for user")
	@PostMapping("/{userId}/favourite/{propertyId}")
	public ResponseEntity<Long> markFavourite(@PathVariable Long userId, @PathVariable Long propertyId) {
		Long jwtUserId = AuthUtils.currentUserId();
		if (jwtUserId == null || !jwtUserId.equals(userId)) {
			log.warn("POST /api/user/{}/favourite/{} - Forbidden: jwtUserId={}", userId, propertyId, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		log.info("POST /api/user/{}/favourite/{} - Toggling favourite", userId, propertyId);
		Long result = userService.markFavourite(userId, propertyId);
		log.info("POST /api/user/{}/favourite/{} - Favourite toggled", userId, propertyId);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Toggle property inquiry for user")
	@PostMapping("/{userId}/interest/{propertyId}")
	public ResponseEntity<Long> markInterested(@PathVariable Long userId, @PathVariable Long propertyId) {
		Long jwtUserId = AuthUtils.currentUserId();
		if (jwtUserId == null || !jwtUserId.equals(userId)) {
			log.warn("POST /api/user/{}/interest/{} - Forbidden: jwtUserId={}", userId, propertyId, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		log.info("POST /api/user/{}/interest/{} - Toggling interest", userId, propertyId);
		Long result = userService.markInterested(userId, propertyId);
		log.info("POST /api/user/{}/interest/{} - Interest toggled", userId, propertyId);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Get user's favourite properties")
	@GetMapping("/{userId}/favourites")
	public ResponseEntity<List<PropertyDto>> getFavouriteProperties(@PathVariable Long userId) {
		Long jwtUserId = AuthUtils.currentUserId();
		if (jwtUserId == null || !jwtUserId.equals(userId)) {
			log.warn("GET /api/user/{}/favourites - Forbidden: jwtUserId={}", userId, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		log.info("GET /api/user/{}/favourites - Fetching favourites", userId);
		List<PropertyDto> favourites = userService.getFavouriteProperties(userId);
		log.info("GET /api/user/{}/favourites - Returned {} favourites", userId, favourites.size());
		return ResponseEntity.ok(favourites);
	}

	@Operation(summary = "Get user's inquired properties")
	@GetMapping("/{userId}/inqueries")
	public ResponseEntity<List<PropertyDto>> getInquiredProperties(@PathVariable Long userId) {
		Long jwtUserId = AuthUtils.currentUserId();
		if (jwtUserId == null || !jwtUserId.equals(userId)) {
			log.warn("GET /api/user/{}/inqueries - Forbidden: jwtUserId={}", userId, jwtUserId);
			return ResponseEntity.status(403).build();
		}
		log.info("GET /api/user/{}/inqueries - Fetching inquired properties", userId);
		List<PropertyDto> inquired = userService.getInquiredProperties(userId);
		log.info("GET /api/user/{}/inqueries - Returned {} inquired properties", userId, inquired.size());
		return ResponseEntity.ok(inquired);
	}
}
