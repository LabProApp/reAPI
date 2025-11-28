package com.api.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.userproperty.UserPropertyRelation;
import com.api.userrelation.UserRelation;

public class UserDto extends BaseEntity {

	private Long id;
	private String name;
	private String email;
	private String password;
	private String address;
	private String mobile;
	private String otp;
	private LocalDateTime otpGeneratedAt;
	private Boolean isVerified = false;
	private MasterEnums.UserStatusEnum userStatus;
	// new attributes

	private MasterEnums.UserRoleEnum userRole; // e.g., "CUSTOMER", "ADMIN", "AGENT","OWNER"

	private MasterEnums.PackageEnum userPackage; // e.g., "Free", "Premium", "Gold"

	private List<UserPropertyRelation> userProperties = new ArrayList<>();

	private List<UserRelation> userRelations = new ArrayList<>();

	private List<UserRelation> relatedUserRelations = new ArrayList<>();

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

	public String getPassword() {
		return password;
	}

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

	public MasterEnums.PackageEnum getUserPackage() {
		return userPackage;
	}

	public void setUserPackage(MasterEnums.PackageEnum userPackage) {
		this.userPackage = userPackage;
	}

	public List<UserPropertyRelation> getUserProperties() {
		return userProperties;
	}

	public void setUserProperties(List<UserPropertyRelation> userProperties) {
		this.userProperties = userProperties;
	}

	public MasterEnums.UserStatusEnum getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(MasterEnums.UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	public List<UserRelation> getUserRelations() {
		return userRelations;
	}

	public void setUserRelations(List<UserRelation> userRelations) {
		this.userRelations = userRelations;
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

	public List<UserRelation> getRelatedUserRelations() {
		return relatedUserRelations;
	}

	public void setRelatedUserRelations(List<UserRelation> relatedUserRelations) {
		this.relatedUserRelations = relatedUserRelations;
	}

}