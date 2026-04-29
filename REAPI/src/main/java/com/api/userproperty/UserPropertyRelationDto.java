package com.api.userproperty;

import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.api.prop.PropertyDto;
import com.api.user.UserDto;

/**
 * Data Transfer Object for {@link UserPropertyRelation}, used in API request
 * and response payloads.
 *
 * <p>Unlike the entity, the DTO's {@link #setFavourite(boolean)} and
 * {@link #setInquiry(boolean)} setters do <em>not</em> auto-set date fields —
 * the date fields are independent and are populated from the database values
 * via {@link #setFavouriteDate(LocalDateTime)} and
 * {@link #setInquiryDate(LocalDateTime)}. This design prevents accidental
 * timestamp overwrites during mapping from the persistence layer.</p>
 *
 * <p>Extends {@link BaseDto} to inherit audit fields.</p>
 */
public class UserPropertyRelationDto extends BaseDto {

	private Long id;

	// --- Relations as DTOs ---
	private UserDto user;
	private PropertyDto property;

	// --- Flags ---
	private boolean favourite = false;
	private boolean inquiry = false;

	// --- Dates ---
	private LocalDateTime favouriteDate;
	private LocalDateTime inquiryDate;

	// --- Comments / Notes ---
	private String comments;

	// --- Status ---
	private MasterEnums.UserInquiryStatusEnum status = MasterEnums.UserInquiryStatusEnum.ACTIVE;

	// --- Constructors ---

	/**
	 * Default no-argument constructor.
	 */
	public UserPropertyRelationDto() {
	}

	// --- Logic for flags ---

	/**
	 * Sets the favourite flag.
	 *
	 * <p>This setter does <em>not</em> modify {@code favouriteDate}; dates must be
	 * set independently via {@link #setFavouriteDate(LocalDateTime)}.</p>
	 *
	 * @param favourite {@code true} to mark as favourite
	 */
	public void setFavourite(boolean favourite) {
		this.favourite = favourite;

	}

	/**
	 * Sets the inquiry flag.
	 *
	 * <p>This setter does <em>not</em> modify {@code inquiryDate}; dates must be
	 * set independently via {@link #setInquiryDate(LocalDateTime)}.</p>
	 *
	 * @param inquiry {@code true} to mark as inquiry
	 */
	public void setInquiry(boolean inquiry) {
		this.inquiry = inquiry;

	}

	// --- Getters and Setters ---

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
	 * Returns the DTO representation of the associated user.
	 *
	 * @return the user DTO
	 */
	public UserDto getUser() {
		return user;
	}

	/**
	 * Sets the DTO representation of the associated user.
	 *
	 * @param user the user DTO
	 */
	public void setUser(UserDto user) {
		this.user = user;
	}

	/**
	 * Returns the DTO representation of the associated property.
	 *
	 * @return the property DTO
	 */
	public PropertyDto getProperty() {
		return property;
	}

	/**
	 * Sets the DTO representation of the associated property.
	 *
	 * @param property the property DTO
	 */
	public void setProperty(PropertyDto property) {
		this.property = property;
	}

	/**
	 * Returns whether the listing is marked as a favourite by the user.
	 *
	 * @return {@code true} if favourited
	 */
	public boolean isFavourite() {
		return favourite;
	}

	/**
	 * Returns whether the user has raised an inquiry for this listing.
	 *
	 * @return {@code true} if an inquiry has been raised
	 */
	public boolean isInquiry() {
		return inquiry;
	}

	/**
	 * Returns the date and time the user favourited this property.
	 *
	 * @return the favourite timestamp, or {@code null} if not set
	 */
	public LocalDateTime getFavouriteDate() {
		return favouriteDate;
	}

	/**
	 * Sets the favourite timestamp (populated from the database).
	 *
	 * @param favouriteDate the favourite timestamp
	 */
	public void setFavouriteDate(LocalDateTime favouriteDate) {
		this.favouriteDate = favouriteDate;
	}

	/**
	 * Returns the date and time the user raised an inquiry.
	 *
	 * @return the inquiry timestamp, or {@code null} if not set
	 */
	public LocalDateTime getInquiryDate() {
		return inquiryDate;
	}

	/**
	 * Sets the inquiry timestamp (populated from the database).
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
	 * Returns the current inquiry status.
	 *
	 * @return the inquiry status enum value
	 */
	public MasterEnums.UserInquiryStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the inquiry status.
	 *
	 * @param status the inquiry status enum value
	 */
	public void setStatus(MasterEnums.UserInquiryStatusEnum status) {
		this.status = status;
	}
}
