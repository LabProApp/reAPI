package com.api.user;

import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public MasterEnums.UserRoleEnum getUserRole() {
		return userRole;
	}

	public void setUserRole(MasterEnums.UserRoleEnum userRole) {
		this.userRole = userRole;
	}

	public MasterEnums.UserStatusEnum getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(MasterEnums.UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	public MasterEnums.PackageEnum getUserPackage() {
		return userPackage;
	}

	public void setUserPackage(MasterEnums.PackageEnum userPackage) {
		this.userPackage = userPackage;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpGeneratedAt() {
		return otpGeneratedAt;
	}

	public void setOtpGeneratedAt(LocalDateTime otpGeneratedAt) {
		this.otpGeneratedAt = otpGeneratedAt;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

}