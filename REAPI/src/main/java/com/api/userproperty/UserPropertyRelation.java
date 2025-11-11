package com.api.userproperty;

import java.time.LocalDateTime;

import com.api.prop.Property;
import com.api.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference(value = "user-userrelation")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id")
	@JsonBackReference(value = "property-userrelation")
	private Property property;

	@Builder.Default
	private boolean isFavourite = false;

	@Builder.Default
	private boolean isInterested = false;

	private LocalDateTime favouriteDate;

	private LocalDateTime interestDate;

	// --- Getters and Setters ---
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

	public boolean isFavourite() {
		return isFavourite;
	}

	public void setFavourite(boolean isFavourite) {
		this.isFavourite = isFavourite;
		this.favouriteDate = isFavourite ? LocalDateTime.now() : null;
	}

	public boolean isInterested() {
		return isInterested;
	}

	public void setInterested(boolean isInterested) {
		this.isInterested = isInterested;
		this.interestDate = isInterested ? LocalDateTime.now() : null;
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

	/*
	 * @JsonProperty("property_id") public Long getPropertyId() { return property !=
	 * null ? property.getId() : null; }
	 */

	
}
