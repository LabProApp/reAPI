package com.api.user;

import java.util.ArrayList;
import java.util.List;

import com.api.BaseEntity;
import com.api.documents.Documents;
import com.api.enums.MasterEnums;
import com.api.userproperty.UserPropertyRelation;
import com.api.userrelation.UserRelation;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User extends  BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(unique = true)
	private String email;
	private String password;
	private String address;
	private String mobile;

	@Enumerated(EnumType.STRING)
	private MasterEnums.UserStatusEnum userStatus;
	// new attributes
	@Enumerated(EnumType.STRING)
	private MasterEnums.UserRoleEnum userRole; // e.g., "CUSTOMER", "ADMIN", "AGENT","OWNER"
	private String userPackage; // e.g., "Free", "Premium", "Gold"

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference(value = "user-documents")
	private List<Documents> documents = new ArrayList<>();

	@JsonManagedReference(value = "user-userrelation")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserPropertyRelation> userProperties = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference(value = "user-main-relations")
	private List<UserRelation> userRelations = new ArrayList<>();

	@OneToMany(mappedBy = "relatedUser", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference(value = "user-related-relations")
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

	public String getUserPackage() {
		return userPackage;
	}

	public void setUserPackage(String userPackage) {
		this.userPackage = userPackage;
	}

	public List<Documents> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
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

}