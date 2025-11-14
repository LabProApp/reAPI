package com.api.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.userproperty.UserPropertyRelation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User APIs", description = "Operations related to User management")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody User user) {
		return userService.signup(user);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {
		return userService.login(user);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return userService.logout();
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
		return userService.resetPassword(email, newPassword);
	}

	@GetMapping("/me")
	@Operation(summary = "Get user by ID", description = "Retrieves user details by unique user ID.")
	public ResponseEntity<?> getProfile(@RequestParam String email) {
		return ResponseEntity.ok(userService.getProfile(email));
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateProfile(@RequestBody User user) {
		return userService.updateProfile(user);
	}

	@PostMapping("/{userId}/favourite/{propertyId}")
	public UserPropertyRelation markFavourite(@PathVariable Long userId, @PathVariable Long propertyId) {
		return userService.markFavourite(userId, propertyId);
	}

	@PostMapping("/{userId}/interest/{propertyId}")
	public UserPropertyRelation markInterested(@PathVariable Long userId, @PathVariable Long propertyId) {
		return userService.markInterested(userId, propertyId);
	}

	@GetMapping("/{userId}/favourites")
	public List<UserPropertyRelation> getFavourites(@PathVariable Long userId) {
		return userService.getFavourites(userId);
	}

	@GetMapping("/{userId}/interests")
	public List<UserPropertyRelation> getInterests(@PathVariable Long userId) {
		return userService.getInterests(userId);
	}

	

}
