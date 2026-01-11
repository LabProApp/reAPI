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

@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs", description = "Operations related to User management")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {
		UserDto savedUser = userService.signup(userDto);
		return ResponseEntity.ok(savedUser);
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<String> verifyOtp(@RequestParam String identifier, @RequestParam String otp) {
		return userService.verifyOtp(identifier, otp);
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestParam String identifier) {
		return userService.resendOtp(identifier);
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
		return userService.login(userDto);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return userService.logout();
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody UserDto userDto) {
		return userService.resetPassword(userDto);
	}

	@GetMapping("/me")
	public ResponseEntity<UserDto> getProfile(@RequestParam String identifier) {
		UserDto userDto = userService.getProfile(identifier);
		return ResponseEntity.ok(userDto);
	}

	@PutMapping("/update")
	public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
		UserDto updatedUser = userService.updateProfile(userDto);
		return ResponseEntity.ok(updatedUser);
	}

	@PostMapping("/{userId}/favourite/{propertyId}")
	public UserPropertyRelationDto markFavourite(@PathVariable Long userId, @PathVariable Long propertyId) {
		return userService.markFavourite(userId, propertyId);
	}

	@PostMapping("/{userId}/interest/{propertyId}")
	public UserPropertyRelationDto markInterested(@PathVariable Long userId, @PathVariable Long propertyId) {
		return userService.markInterested(userId, propertyId);
	}

	@GetMapping("/{userId}/favourites")
	public List<PropertyDto> getFavouriteProperties(@PathVariable Long userId) {
		return userService.getFavouriteProperties(userId);
	}

	@GetMapping("/{userId}/inqueries")
	public List<PropertyDto> getInquiredProperties(@PathVariable Long userId) {
		return userService.getInquiredProperties(userId);
	}
}
