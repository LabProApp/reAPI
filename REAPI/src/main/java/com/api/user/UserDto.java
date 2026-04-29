package com.api.user;

import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Data Transfer Object for {@link User} used across API request and response payloads.
 *
 * <p>Sensitive fields are intentionally hidden from serialised output:
 * <ul>
 *   <li>{@code password} – the getter is annotated {@code @JsonIgnore} so the hash is never
 *       returned in a response; the setter carries {@code @JsonProperty(WRITE_ONLY)} so the
 *       plain-text value can be received during signup/reset.</li>
 *   <li>{@code otp} and {@code otpGeneratedAt} – both annotated {@code @JsonIgnore} and are
 *       never exposed to API consumers.</li>
 * </ul>
 */
public class UserDto extends BaseDto {

	private Long id;
	private String name;
	private String email;
	private String password;
	private String address;
	private String mobile;

	@JsonIgnore
	private String otp;
	@JsonIgnore
	private LocalDateTime otpGeneratedAt;
	private Boolean isVerified = false;
	private MasterEnums.UserStatusEnum userStatus;
	// new attributes

	private MasterEnums.UserRoleEnum userRole; // e.g., "CUSTOMER", "ADMIN", "AGENT","OWNER"

	private MasterEnums.PackageEnum userPackage; // e.g., "Free", "Premium", "Gold"

	/** Returns the unique identifier of the user. */
	public Long getId() {
		return id;
	}

	/** Sets the unique identifier of the user. */
	public void setId(Long id) {
		this.id = id;
	}

	/** Returns the full name of the user. */
	public String getName() {
		return name;
	}

	/** Sets the full name of the user. */
	public void setName(String name) {
		this.name = name;
	}

	/** Returns the email address of the user. */
	public String getEmail() {
		return email;
	}

	/** Sets the email address of the user. */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the password value held in this DTO.
	 *
	 * <p>Annotated {@code @JsonIgnore} – this value is <strong>never</strong> included
	 * in JSON responses to prevent leaking credentials or BCrypt hashes.
	 *
	 * @return the raw or hashed password string
	 */
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password on this DTO from an inbound JSON request.
	 *
	 * <p>Annotated {@code @JsonProperty(WRITE_ONLY)} – the field is accepted during
	 * deserialization (e.g., signup, reset-password) but is never serialized back.
	 *
	 * @param password the plain-text password supplied by the caller
	 */
	@JsonProperty(access = Access.WRITE_ONLY)
	public void setPassword(String password) {
		this.password = password;
	}

	/** Returns the postal/street address of the user. */
	public String getAddress() {
		return address;
	}

	/** Sets the postal/street address of the user. */
	public void setAddress(String address) {
		this.address = address;
	}

	/** Returns the mobile phone number of the user. */
	public String getMobile() {
		return mobile;
	}

	/** Sets the mobile phone number of the user. */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/** Returns the role assigned to this user (e.g., ADMIN, AGENT, CLIENT). */
	public MasterEnums.UserRoleEnum getUserRole() {
		return userRole;
	}

	/** Sets the role assigned to this user (e.g., ADMIN, AGENT, CLIENT). */
	public void setUserRole(MasterEnums.UserRoleEnum userRole) {
		this.userRole = userRole;
	}

	/** Returns the current account status of the user. */
	public MasterEnums.UserStatusEnum getUserStatus() {
		return userStatus;
	}

	/** Sets the current account status of the user. */
	public void setUserStatus(MasterEnums.UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	/** Returns the subscription package tier of the user. */
	public MasterEnums.PackageEnum getUserPackage() {
		return userPackage;
	}

	/** Sets the subscription package tier of the user. */
	public void setUserPackage(MasterEnums.PackageEnum userPackage) {
		this.userPackage = userPackage;
	}

	/**
	 * Returns the OTP value currently held in this DTO.
	 *
	 * <p>Annotated {@code @JsonIgnore} – this field is never serialized into API responses.
	 *
	 * @return the one-time password string
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * Sets the OTP value on this DTO.
	 *
	 * <p>Annotated {@code @JsonIgnore} – this field is never serialized into API responses.
	 *
	 * @param otp the one-time password string
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}

	/**
	 * Returns the timestamp when the OTP was generated.
	 *
	 * <p>Annotated {@code @JsonIgnore} – this field is never serialized into API responses.
	 *
	 * @return the OTP generation timestamp
	 */
	public LocalDateTime getOtpGeneratedAt() {
		return otpGeneratedAt;
	}

	/**
	 * Sets the timestamp when the OTP was generated.
	 *
	 * <p>Annotated {@code @JsonIgnore} – this field is never serialized into API responses.
	 *
	 * @param otpGeneratedAt the OTP generation timestamp
	 */
	public void setOtpGeneratedAt(LocalDateTime otpGeneratedAt) {
		this.otpGeneratedAt = otpGeneratedAt;
	}

	/** Returns {@code true} if the user has completed OTP verification. */
	public Boolean getIsVerified() {
		return isVerified;
	}

	/** Sets whether the user has completed OTP verification. */
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

}
