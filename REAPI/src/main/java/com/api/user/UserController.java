package com.api.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.prop.PropertyDto;
import com.api.userproperty.UserPropertyRelationDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing user management endpoints under {@code /api/user}.
 *
 * <p>Handles user registration, OTP verification, authentication (login/logout),
 * profile retrieval and updates, password reset, and user-property interactions
 * such as marking favourites and submitting inquiries.
 *
 * <p>All business logic is delegated to {@link UserService}.
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs", description = "Operations related to User management")
public class UserController {

	private final UserService userService;

	/**
	 * Constructs a {@code UserController} with the required service dependency.
	 *
	 * @param userService the service handling user business logic
	 */
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Registers a new user account.
	 *
	 * <p>{@code POST /api/user/signup}
	 *
	 * @param userDto the registration payload; must contain at minimum an email or mobile
	 *                and a password
	 * @return {@code 200 OK} with the created {@link UserDto} (password excluded)
	 */
	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {
		log.info("POST /api/user/signup - Signup attempt for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		UserDto savedUser = userService.signup(userDto);
		log.info("POST /api/user/signup - User created with id={}", savedUser.getId());
		return ResponseEntity.ok(savedUser);
	}

	/**
	 * Verifies the OTP submitted by the user to activate the account.
	 *
	 * <p>{@code POST /api/user/verifyOtp}
	 *
	 * @param identifier the user's email address or mobile number
	 * @param otp        the one-time password to verify
	 * @return {@code 200 OK} on success, or {@code 400 Bad Request} if invalid/expired
	 */
	@PostMapping("/verifyOtp")
	public ResponseEntity<String> verifyOtp(@RequestParam String identifier, @RequestParam String otp) {
		log.info("POST /api/user/verifyOtp - OTP verification request for identifier={}", identifier);
		ResponseEntity<String> response = userService.verifyOtp(identifier, otp);
		log.info("POST /api/user/verifyOtp - OTP verification result for identifier={}, status={}",
				identifier, response.getStatusCode());
		return response;
	}

	/**
	 * Re-sends a fresh OTP to the user's registered email or mobile.
	 *
	 * <p>{@code POST /api/user/resend-otp}
	 *
	 * @param identifier the user's email address or mobile number
	 * @return {@code 200 OK} with a confirmation message, or {@code 400} if the
	 *         60-second cooldown has not elapsed
	 */
	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestParam String identifier) {
		log.info("POST /api/user/resend-otp - Resend OTP request for identifier={}", identifier);
		ResponseEntity<String> response = userService.resendOtp(identifier);
		log.info("POST /api/user/resend-otp - Resend OTP result for identifier={}, status={}",
				identifier, response.getStatusCode());
		return response;
	}

	/**
	 * Authenticates a user and returns a JWT bearer token on success.
	 *
	 * <p>{@code POST /api/user/login}
	 *
	 * @param userDto the login request containing an email or mobile and plain-text password
	 * @return {@code 200 OK} with a {@link LoginResponse}, {@code 401} on bad credentials,
	 *         or {@code 400} if neither identifier is supplied
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserDto userDto) {
		log.info("POST /api/user/login - Login attempt for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		ResponseEntity<LoginResponse> response = userService.login(userDto);
		log.info("POST /api/user/login - Login result status={}", response.getStatusCode());
		return response;
	}

	/**
	 * Logs the current user out of the session.
	 *
	 * <p>{@code POST /api/user/logout}
	 *
	 * <p>Because JWT authentication is stateless, this endpoint simply returns a confirmation
	 * message; token invalidation is handled client-side.
	 *
	 * @return {@code 200 OK} with a logout confirmation message
	 */
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		log.info("POST /api/user/logout - Logout request");
		return userService.logout();
	}

	/**
	 * Resets the authenticated user's password.
	 *
	 * <p>{@code POST /api/user/reset-password}
	 *
	 * @param userDto the DTO containing the identifier (email or mobile) and the new password
	 * @return {@code 200 OK} with a success message
	 */
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody UserDto userDto) {
		log.info("POST /api/user/reset-password - Password reset request for identifier={}",
				userDto.getEmail() != null ? userDto.getEmail() : userDto.getMobile());
		ResponseEntity<String> response = userService.resetPassword(userDto);
		log.info("POST /api/user/reset-password - Password reset completed, status={}", response.getStatusCode());
		return response;
	}

	/**
	 * Retrieves the profile of the user identified by email or mobile.
	 *
	 * <p>{@code GET /api/user/me?identifier=...}
	 *
	 * @param identifier the user's email address or mobile number
	 * @return {@code 200 OK} with the {@link UserDto} for the matched user
	 */
	@GetMapping("/me")
	public ResponseEntity<UserDto> getProfile(@RequestParam String identifier) {
		log.info("GET /api/user/me - Fetching profile for identifier={}", identifier);
		UserDto userDto = userService.getProfile(identifier);
		log.info("GET /api/user/me - Profile fetched for userId={}", userDto.getId());
		return ResponseEntity.ok(userDto);
	}

	/**
	 * Retrieves the profile of the user identified by their numeric user ID.
	 *
	 * <p>{@code GET /api/user/profile/{userId}}
	 *
	 * @param userId the surrogate primary key of the user
	 * @return {@code 200 OK} with the {@link UserDto} for the matched user
	 */
	@GetMapping("/profile/{userId}")
	public ResponseEntity<UserDto> getProfilebyUserId(@PathVariable Long userId) {
		log.info("GET /api/user/profile/{} - Fetching profile", userId);
		UserDto userDto = userService.getProfilebyUserId(userId);
		log.info("GET /api/user/profile/{} - Profile fetched", userId);
		return ResponseEntity.ok(userDto);
	}

	/**
	 * Partially updates the authenticated user's profile.
	 *
	 * <p>{@code PUT /api/user/update}
	 *
	 * @param userDto the DTO containing the fields to update; {@code id} must be present
	 * @return {@code 200 OK} with the updated {@link UserDto}
	 */
	@PutMapping("/update")
	public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
		log.info("PUT /api/user/update - Updating profile for userId={}", userDto.getId());
		UserDto updatedUser = userService.updateProfile(userDto);
		log.info("PUT /api/user/update - Profile updated for userId={}", updatedUser.getId());
		return ResponseEntity.ok(updatedUser);
	}

	/**
	 * Toggles the favourite status of a property for the specified user.
	 *
	 * <p>{@code POST /api/user/{userId}/favourite/{propertyId}}
	 *
	 * @param userId     the ID of the user performing the action
	 * @param propertyId the ID of the property to toggle
	 * @return the {@code propertyId} that was toggled
	 */
	@PostMapping("/{userId}/favourite/{propertyId}")
	public Long markFavourite(@PathVariable Long userId, @PathVariable Long propertyId) {
		log.info("POST /api/user/{}/favourite/{} - Toggling favourite", userId, propertyId);
		Long result = userService.markFavourite(userId, propertyId);
		log.info("POST /api/user/{}/favourite/{} - Favourite toggled", userId, propertyId);
		return result;
	}

	/**
	 * Toggles the inquiry (interest) status of a property for the specified user.
	 *
	 * <p>{@code POST /api/user/{userId}/interest/{propertyId}}
	 *
	 * @param userId     the ID of the user performing the action
	 * @param propertyId the ID of the property to toggle
	 * @return the {@code propertyId} that was toggled
	 */
	@PostMapping("/{userId}/interest/{propertyId}")
	public Long markInterested(@PathVariable Long userId, @PathVariable Long propertyId) {
		log.info("POST /api/user/{}/interest/{} - Toggling interest", userId, propertyId);
		Long result = userService.markInterested(userId, propertyId);
		log.info("POST /api/user/{}/interest/{} - Interest toggled", userId, propertyId);
		return result;
	}

	/**
	 * Returns all properties that the specified user has marked as favourites.
	 *
	 * <p>{@code GET /api/user/{userId}/favourites}
	 *
	 * @param userId the ID of the user whose favourites are to be retrieved
	 * @return a list of {@link PropertyDto} objects for the user's favourite properties
	 */
	@GetMapping("/{userId}/favourites")
	public List<PropertyDto> getFavouriteProperties(@PathVariable Long userId) {
		log.info("GET /api/user/{}/favourites - Fetching favourites", userId);
		List<PropertyDto> favourites = userService.getFavouriteProperties(userId);
		log.info("GET /api/user/{}/favourites - Returned {} favourites", userId, favourites.size());
		return favourites;
	}

	/**
	 * Returns all properties for which the specified user has submitted an inquiry.
	 *
	 * <p>{@code GET /api/user/{userId}/inqueries}
	 *
	 * @param userId the ID of the user whose inquired properties are to be retrieved
	 * @return a list of {@link PropertyDto} objects for the user's inquired properties
	 */
	@GetMapping("/{userId}/inqueries")
	public List<PropertyDto> getInquiredProperties(@PathVariable Long userId) {
		log.info("GET /api/user/{}/inqueries - Fetching inquired properties", userId);
		List<PropertyDto> inquired = userService.getInquiredProperties(userId);
		log.info("GET /api/user/{}/inqueries - Returned {} inquired properties", userId, inquired.size());
		return inquired;
	}
}
