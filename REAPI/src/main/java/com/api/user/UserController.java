package com.api.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.prop.Property;
import com.api.userproperty.UserPropertyRelation;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return userService.login(userDto);
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
    @Operation(summary = "Get user by email", description = "Retrieves user details by unique email.")
    public ResponseEntity<UserDto> getProfile(@RequestParam String email) {
        UserDto userDto = userService.getProfile(email);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(userDto);
        return ResponseEntity.ok(updatedUser);
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
    public List<Property> getFavouriteProperties(@PathVariable Long userId) {
        return userService.getFavouriteProperties(userId);
    }

    @GetMapping("/{userId}/inqueries")
    public List<Property> getInquiredProperties(@PathVariable Long userId) {
        return userService.getInquiredProperties(userId);
    }
}
