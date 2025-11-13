package com.api.userproperty;

import java.time.LocalDateTime;

import com.api.enums.MasterEnums;
import com.api.prop.Property;
import com.api.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the relationship between a User and a Property. Stores
 * interest/favourite status, timestamps, user comments, and relation status.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_property_relation", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id",
		"property_id" }))
public class UserPropertyRelation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// --- Relations ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference(value = "user-userrelation")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id")
	@JsonBackReference(value = "property-userrelation")
	private Property property;

	// --- Flags ---
	@Builder.Default
	private boolean isFavourite = false;

	@Builder.Default
	private boolean isInterested = false;

	// --- Dates ---
	private LocalDateTime favouriteDate;
	private LocalDateTime interestDate;

	// --- Comments / Notes ---
	@Column(length = 1000)
	private String comments;

	// --- Status (using a predefined Enum for consistency) ---
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private MasterEnums.UserInquiryStatusEnum status = MasterEnums.UserInquiryStatusEnum.ACTIVE;

	// --- Logic ---

	public void setFavourite(boolean isFavourite) {
		this.isFavourite = isFavourite;
		this.favouriteDate = isFavourite ? LocalDateTime.now() : null;
	}

	public void setInterested(boolean isInterested) {
		this.isInterested = isInterested;
		this.interestDate = isInterested ? LocalDateTime.now() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public LocalDateTime getFavouriteDate() {
		return favouriteDate;
	}

	public void setFavouriteDate(LocalDateTime favouriteDate) {
		this.favouriteDate = favouriteDate;
	}

	public LocalDateTime getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(LocalDateTime interestDate) {
		this.interestDate = interestDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public MasterEnums.UserInquiryStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MasterEnums.UserInquiryStatusEnum status) {
		this.status = status;
	}

	public boolean isFavourite() {
		return isFavourite;
	}

	public boolean isInterested() {
		return isInterested;
	}
}
