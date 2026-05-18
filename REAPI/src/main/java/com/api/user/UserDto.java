package com.api.user;

import java.time.LocalDateTime;
import java.util.Map;

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

	private MasterEnums.PackageEnum userPackage; // BASIC / DELUX / PREMIUM (legacy: REGULAR / ELITE)

	/** Annual price (INR) of the active plan; 0 for BASIC. Read-only — sourced from the plans table. */
	private Double planPriceYearly;

	/** Max number of property listings the user can post on their plan. 0 for BASIC. Read-only. */
	private Integer planPropertyLimit;

	/** Feature flags the client uses to gate UI (e.g. {@code buy_sell}, {@code bank_loans}). Read-only. */
	private Map<String, Boolean> featureFlags;

	
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


	public Double getPlanPriceYearly() {
		return planPriceYearly;
	}


	public void setPlanPriceYearly(Double planPriceYearly) {
		this.planPriceYearly = planPriceYearly;
	}


	public Integer getPlanPropertyLimit() {
		return planPropertyLimit;
	}


	public void setPlanPropertyLimit(Integer planPropertyLimit) {
		this.planPropertyLimit = planPropertyLimit;
	}


	public Map<String, Boolean> getFeatureFlags() {
		return featureFlags;
	}


	public void setFeatureFlags(Map<String, Boolean> featureFlags) {
		this.featureFlags = featureFlags;
	}

}
