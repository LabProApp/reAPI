package com.api.userproperty;

import java.time.LocalDateTime;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.prop.Property;
import com.api.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.NoArgsConstructor;

/**
 * JPA entity that tracks the relationship between a platform user and a
 * property listing.
 *
 * <p>Each row is unique per {@code (user_id, property_id)} pair and records
 * two interaction flags:</p>
 * <ul>
 *   <li><b>favourite</b> — whether the user has favourited the listing;
 *       calling {@link #setFavourite(boolean)} automatically updates
 *       {@link #favouriteDate} to the current time when set to {@code true},
 *       or clears it when set to {@code false}.</li>
 *   <li><b>inquiry</b> — whether the user has raised an inquiry for the
 *       listing; calling {@link #setInquiry(boolean)} similarly auto-manages
 *       {@link #inquiryDate}.</li>
 * </ul>
 *
 * <p>Extends {@link BaseEntity} for audit fields. Relationships to
 * {@link User} and {@link Property} are lazy-loaded and serialisation-excluded
 * via {@code @JsonIgnore}.</p>
 */
@Entity

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_property_relation", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id",
		"property_id" }))
public class UserPropertyRelation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// --- Relations ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	//@JsonBackReference(value = "user-userrelation")
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "property_id")
//	@JsonBackReference(value = "property-userrelation")
	@JsonIgnore
	private Property property;

	// --- Flags ---
	@Builder.Default
	private boolean favourite = false;

	// renamed from isInterested → isInquiry
	@Builder.Default
	private boolean inquiry = false;

	// --- Dates ---
	private LocalDateTime favouriteDate;

	// renamed from interestDate → inquiryDate
	private LocalDateTime inquiryDate;

	// --- Comments / Notes ---
	@Column(length = 1000)
	private String comments;

	// --- Status ---
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private MasterEnums.UserInquiryStatusEnum status = MasterEnums.UserInquiryStatusEnum.ACTIVE;

	// --- Logic ---

	/**
	 * Sets the favourite flag and automatically updates the favourite timestamp.
	 *
	 * <p>When {@code isFavourite} is {@code true}, {@link #favouriteDate} is set
	 * to the current date-time. When {@code false}, it is cleared to {@code null}.</p>
	 *
	 * @param isFavourite {@code true} to mark as favourite; {@code false} to remove
	 */
	public void setFavourite(boolean isFavourite) {
		this.favourite = isFavourite;
		this.favouriteDate = isFavourite ? LocalDateTime.now() : null;
	}

	/**
	 * Sets the inquiry flag and automatically updates the inquiry timestamp.
	 *
	 * <p>When {@code isInquiry} is {@code true}, {@link #inquiryDate} is set to
	 * the current date-time. When {@code false}, it is cleared to {@code null}.</p>
	 *
	 * @param isInquiry {@code true} to mark as inquiry; {@code false} to remove
	 */
	// renamed logic
	public void setInquiry(boolean isInquiry) {
		this.inquiry = isInquiry;
		this.inquiryDate = isInquiry ? LocalDateTime.now() : null;
	}

	/**
	 * Returns whether this listing is marked as a favourite by the user.
	 *
	 * @return {@code true} if the listing is favourited
	 */
	public boolean getFavourite() {
		return favourite;
	}

	/**
	 * Returns whether the user has raised an inquiry for this listing.
	 *
	 * @return {@code true} if an inquiry has been raised
	 */
	public boolean getInquiry() {
		return inquiry;
	}

	/**
	 * Returns the primary key of this relation record.
	 *
	 * @return the relation ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the primary key of this relation record.
	 *
	 * @param id the relation ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the user associated with this relation.
	 *
	 * @return the {@link User}
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user associated with this relation.
	 *
	 * @param user the {@link User}
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the property associated with this relation.
	 *
	 * @return the {@link Property}
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * Sets the property associated with this relation.
	 *
	 * @param property the {@link Property}
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

	/**
	 * Returns the date and time the user favourited this property.
	 *
	 * @return the favourite timestamp, or {@code null} if not favourited
	 */
	public LocalDateTime getFavouriteDate() {
		return favouriteDate;
	}

	/**
	 * Sets the favourite timestamp directly (used for persistence, not for business logic).
	 *
	 * @param favouriteDate the favourite timestamp
	 */
	public void setFavouriteDate(LocalDateTime favouriteDate) {
		this.favouriteDate = favouriteDate;
	}

	/**
	 * Returns the date and time the user raised an inquiry for this property.
	 *
	 * @return the inquiry timestamp, or {@code null} if no inquiry has been raised
	 */
	public LocalDateTime getInquiryDate() {
		return inquiryDate;
	}

	/**
	 * Sets the inquiry timestamp directly (used for persistence, not for business logic).
	 *
	 * @param inquiryDate the inquiry timestamp
	 */
	public void setInquiryDate(LocalDateTime inquiryDate) {
		this.inquiryDate = inquiryDate;
	}

	/**
	 * Returns the user's comments or notes about this property.
	 *
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the user's comments or notes about this property.
	 *
	 * @param comments the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Returns the current inquiry status of this relation.
	 *
	 * @return the inquiry status enum value
	 */
	public MasterEnums.UserInquiryStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the inquiry status of this relation.
	 *
	 * @param status the inquiry status enum value
	 */
	public void setStatus(MasterEnums.UserInquiryStatusEnum status) {
		this.status = status;
	}
}
