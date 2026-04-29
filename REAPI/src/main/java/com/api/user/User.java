package com.api.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.userproperty.UserPropertyRelation;
import com.api.userrelation.UserRelation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**
 * JPA entity representing a registered user in the real estate platform.
 *
 * <p>Maps to the {@code users} database table and extends {@link com.api.commons.BaseEntity}
 * for common audit fields. Each user may have a role ({@link MasterEnums.UserRoleEnum}),
 * a subscription package ({@link MasterEnums.PackageEnum}), and relationships to
 * {@link UserPropertyRelation} and {@link UserRelation} records.
 *
 * <p>Passwords are stored as BCrypt hashes. OTP fields support the email/SMS
 * one-time-password verification flow.
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(unique = true)
	private String email;
	private String password;
	private String address;
	private String mobile;

	// ---------- OTP Fields ----------
	private String otp;

	private LocalDateTime otpGeneratedAt;

	// Optional: mark if verified after OTP
	private Boolean isVerified = false;

	@Enumerated(EnumType.STRING)
	private MasterEnums.UserStatusEnum userStatus;
	// new attributes
	@Enumerated(EnumType.STRING)
	private MasterEnums.UserRoleEnum userRole; // e.g., "CUSTOMER", "ADMIN", "AGENT","OWNER"

	@Enumerated(EnumType.STRING)
	private  MasterEnums.PackageEnum userPackage; // e.g., "Free", "Premium", "Gold"

	//@JsonManagedReference(value = "user-userrelation")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<UserPropertyRelation> userProperties = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	//@JsonManagedReference(value = "user-main-relations")
	@JsonIgnore
	private List<UserRelation> userRelations = new ArrayList<>();

	@OneToMany(mappedBy = "relatedUser", cascade = CascadeType.ALL, orphanRemoval = true)
	//@JsonManagedReference(value = "user-related-relations")
	@JsonIgnore
	private List<UserRelation> relatedUserRelations = new ArrayList<>();

	/** Returns the unique surrogate identifier for this user. */
	public Long getId() {
		return id;
	}

	/** Sets the unique surrogate identifier for this user. */
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

	/** Returns the unique email address of the user. */
	public String getEmail() {
		return email;
	}

	/** Sets the unique email address of the user. */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Returns the BCrypt-hashed password of the user. */
	public String getPassword() {
		return password;
	}

	/** Sets the BCrypt-hashed password of the user. */
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

	/** Returns the subscription package tier of this user. */
	public MasterEnums.PackageEnum getUserPackage() {
		return userPackage;
	}

	/** Sets the subscription package tier of this user. */
	public void setUserPackage(MasterEnums.PackageEnum userPackage) {
		this.userPackage = userPackage;
	}

	/** Returns all property relations (favourites, inquiries) associated with this user. */
	public List<UserPropertyRelation> getUserProperties() {
		return userProperties;
	}

	/** Sets all property relations associated with this user. */
	public void setUserProperties(List<UserPropertyRelation> userProperties) {
		this.userProperties = userProperties;
	}

	/** Returns the current account status of the user (e.g., ACTIVE, PENDING). */
	public MasterEnums.UserStatusEnum getUserStatus() {
		return userStatus;
	}

	/** Sets the current account status of the user (e.g., ACTIVE, PENDING). */
	public void setUserStatus(MasterEnums.UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	/** Returns the list of user-to-user relations where this user is the primary party. */
	public List<UserRelation> getUserRelations() {
		return userRelations;
	}

	/** Sets the list of user-to-user relations where this user is the primary party. */
	public void setUserRelations(List<UserRelation> userRelations) {
		this.userRelations = userRelations;
	}

	/** Returns the current one-time password (OTP) generated for this user. */
	public String getOtp() {
		return otp;
	}

	/** Sets the current one-time password (OTP) for this user. */
	public void setOtp(String otp) {
		this.otp = otp;
	}

	/** Returns the timestamp at which the current OTP was generated. */
	public LocalDateTime getOtpGeneratedAt() {
		return otpGeneratedAt;
	}

	/** Sets the timestamp at which the current OTP was generated. */
	public void setOtpGeneratedAt(LocalDateTime otpGeneratedAt) {
		this.otpGeneratedAt = otpGeneratedAt;
	}

	/** Returns {@code true} if the user has successfully completed OTP verification. */
	public Boolean getIsVerified() {
		return isVerified;
	}

	/** Sets whether the user has completed OTP verification. */
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	/** Returns the list of user-to-user relations where this user is the related (secondary) party. */
	public List<UserRelation> getRelatedUserRelations() {
		return relatedUserRelations;
	}

	/** Sets the list of user-to-user relations where this user is the related (secondary) party. */
	public void setRelatedUserRelations(List<UserRelation> relatedUserRelations) {
		this.relatedUserRelations = relatedUserRelations;
	}

}
