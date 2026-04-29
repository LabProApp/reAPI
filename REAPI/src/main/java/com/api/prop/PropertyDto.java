package com.api.prop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.commons.BaseDto;
import com.api.documents.DocumentminDto;
import com.api.enums.MasterEnums;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object that mirrors the {@link Property} entity for use in API
 * request and response payloads.
 *
 * <p>Extends {@link BaseDto} to inherit audit fields. Adds a
 * {@code documentList} field that is populated from S3 pre-signed URLs by the
 * service layer so that callers receive document links without a separate
 * request.</p>
 *
 * <p>Amenities are stored as a comma-separated string of integer IDs (e.g.
 * {@code "1,2,3"}). Use {@link #setAmenitiesFromList(List)} to populate from a
 * list and {@link #getAmenitiesAsList()} to read back as a typed list.</p>
 */
public class PropertyDto extends BaseDto {

	private Long id;

	// ================= BASIC INFO =================

	@NotBlank(message = "Title is required")
	private String title;


	private String address;

	@NotBlank(message = "City is required")
	private String city;

	@NotBlank(message = "State is required")
	private String state;

	private MasterEnums.PropertyStatusEnum propertyStatus;

	@NotBlank(message = "Type is required (Apartment, Villa, Plot, Office)")
	private String type;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private Double price;

	@Min(value = 0)
	private Integer bedrooms;

	@Min(value = 0)
	private Integer bathrooms;

	@NotBlank(message = "Location is required")
	private String location;

	private Double carpetArea;
	private Double superArea;

	private String amenities; // "1,2,3"

	@NotBlank(message = "PostedBy is required")
	private String postedBy; // Owner, Broker, Builder

	private String contactNumber;

	@NotBlank(message = "Construction status is required")
	private String constructionStatus;

	private String currency = "INR";

	private LocalDate readyDate;

	@NotBlank(message = "Category is required")
	private String category; // Residential, Commercial

	private String projectName;

	private String description;

	private Long postedByUser;

	private LocalDateTime postDate = LocalDateTime.now();

	@NotBlank(message = "Rent/Sale is required")
	private String rentOrSale; // Rent / Sale

	private boolean verified;

	// ================= LOCATION DETAILS =================

	private String landmark;
	private Double latitude;
	private Double longitude;

	// ================= BUILDING DETAILS =================

	private Integer floorNumber;
	private Integer totalFloors;

	private String parkingCount;
	private String parkingType; // Covered, Open, Both

	private String facing;
	private String propertyAge;
	private String ownershipType; // Freehold, Leasehold
	private String furnishing;
	private Boolean negotiable;
	private Boolean loanAvailable;

	// ================= RENT DETAILS =================

	private Double monthlyRent;
	private Double securityDeposit;
	private Double brokerage;
	private String preferredTenants; // Family, Bachelor
	private Boolean petsAllowed;
	private Boolean nonVegAllowed;
	private String leaseDuration;
	private String noticePeriod;
	private Boolean maintenanceIncluded;

	// ================= PROJECT / BUILDER =================

	private String builderName;
	private Boolean reraApproved;
	private String reraNumber;

	// ================= SYSTEM METRICS =================

	private Integer viewsCount;
	private Integer shortListCount;
	private List<DocumentminDto> documentList;

	// ================= AMENITIES HELPERS =================

	/**
	 * Sets the {@code amenities} field from a list of amenity IDs.
	 *
	 * <p>The IDs are joined into a comma-separated string (e.g. {@code "1,2,3"}).
	 * If the list is {@code null} or empty the field is set to {@code null}.</p>
	 *
	 * @param amenityIds list of integer amenity IDs; may be {@code null} or empty
	 */
	public void setAmenitiesFromList(List<Integer> amenityIds) {
		if (amenityIds != null && !amenityIds.isEmpty()) {
			this.amenities = amenityIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		} else {
			this.amenities = null;
		}
	}

	/**
	 * Returns the amenities as a typed list of integer IDs parsed from the stored
	 * comma-separated string.
	 *
	 * @return list of amenity IDs, or an empty list if no amenities are set
	 */
	public List<Integer> getAmenitiesAsList() {
		if (this.amenities == null || this.amenities.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(this.amenities.split(",")).map(Integer::valueOf).collect(Collectors.toList());
	}

	/**
	 * Returns the property ID.
	 *
	 * @return the ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the property ID.
	 *
	 * @param id the ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the listing title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the listing title.
	 *
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the street address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the street address.
	 *
	 * @param address the address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Returns the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the listing status enum value.
	 *
	 * @return the property status
	 */
	public MasterEnums.PropertyStatusEnum getPropertyStatus() {
		return propertyStatus;
	}

	/**
	 * Sets the listing status enum value.
	 *
	 * @param propertyStatus the property status
	 */
	public void setPropertyStatus(MasterEnums.PropertyStatusEnum propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	/**
	 * Returns the property type (e.g., Apartment, Villa, Plot, Office).
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the property type.
	 *
	 * @param type the type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the listed price.
	 *
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * Sets the listed price.
	 *
	 * @param price the price; must be greater than 0
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * Returns the list of documents (pre-signed S3 URLs) attached to this property.
	 *
	 * @return the document list
	 */
	public List<DocumentminDto> getDocumentList() {
		return documentList;
	}

	/**
	 * Sets the list of documents attached to this property.
	 *
	 * @param documentList the document list
	 */
	public void setDocumentList(List<DocumentminDto> documentList) {
		this.documentList = documentList;
	}

	/**
	 * Returns the number of bedrooms.
	 *
	 * @return the bedroom count
	 */
	public Integer getBedrooms() {
		return bedrooms;
	}

	/**
	 * Sets the number of bedrooms.
	 *
	 * @param bedrooms the bedroom count; must be non-negative
	 */
	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	/**
	 * Returns the number of bathrooms.
	 *
	 * @return the bathroom count
	 */
	public Integer getBathrooms() {
		return bathrooms;
	}

	/**
	 * Sets the number of bathrooms.
	 *
	 * @param bathrooms the bathroom count; must be non-negative
	 */
	public void setBathrooms(Integer bathrooms) {
		this.bathrooms = bathrooms;
	}

	/**
	 * Returns the locality / micro-location within the city.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the locality / micro-location within the city.
	 *
	 * @param location the location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the carpet area in square feet.
	 *
	 * @return the carpet area
	 */
	public Double getCarpetArea() {
		return carpetArea;
	}

	/**
	 * Sets the carpet area in square feet.
	 *
	 * @param carpetArea the carpet area
	 */
	public void setCarpetArea(Double carpetArea) {
		this.carpetArea = carpetArea;
	}

	/**
	 * Returns the super built-up area in square feet.
	 *
	 * @return the super area
	 */
	public Double getSuperArea() {
		return superArea;
	}

	/**
	 * Sets the super built-up area in square feet.
	 *
	 * @param superArea the super area
	 */
	public void setSuperArea(Double superArea) {
		this.superArea = superArea;
	}

	/**
	 * Returns the raw comma-separated amenity ID string.
	 *
	 * @return the amenities string
	 */
	public String getAmenities() {
		return amenities;
	}

	/**
	 * Returns the furnishing status (e.g., Furnished, Semi-Furnished, Unfurnished).
	 *
	 * @return the furnishing status
	 */
	public String getFurnishing() {
	    return furnishing;
	}

	/**
	 * Sets the furnishing status.
	 *
	 * @param furnishing the furnishing status
	 */
	public void setFurnishing(String furnishing) {
	    this.furnishing = furnishing;
	}

	/**
	 * Sets the raw comma-separated amenity ID string.
	 *
	 * @param amenities the amenities string
	 */
	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	/**
	 * Returns who posted the listing (e.g., Owner, Broker, Builder).
	 *
	 * @return the postedBy value
	 */
	public String getPostedBy() {
		return postedBy;
	}

	/**
	 * Sets who posted the listing.
	 *
	 * @param postedBy the postedBy value
	 */
	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	/**
	 * Returns the contact number for enquiries.
	 *
	 * @return the contact number
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * Sets the contact number for enquiries.
	 *
	 * @param contactNumber the contact number
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * Returns the construction status.
	 *
	 * @return the construction status
	 */
	public String getConstructionStatus() {
		return constructionStatus;
	}

	/**
	 * Sets the construction status.
	 *
	 * @param constructionStatus the construction status
	 */
	public void setConstructionStatus(String constructionStatus) {
		this.constructionStatus = constructionStatus;
	}

	/**
	 * Returns the currency code for the price (default: {@code "INR"}).
	 *
	 * @return the currency code
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency code for the price.
	 *
	 * @param currency the currency code
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * Returns the possession / ready-to-move date.
	 *
	 * @return the ready date
	 */
	public LocalDate getReadyDate() {
		return readyDate;
	}

	/**
	 * Sets the possession / ready-to-move date.
	 *
	 * @param readyDate the ready date
	 */
	public void setReadyDate(LocalDate readyDate) {
		this.readyDate = readyDate;
	}

	/**
	 * Returns the property category (e.g., Residential, Commercial).
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the property category.
	 *
	 * @param category the category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Returns the project or society name.
	 *
	 * @return the project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the project or society name.
	 *
	 * @param projectName the project name
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Returns the detailed description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the detailed description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the ID of the platform user who posted this property.
	 *
	 * @return the poster's user ID
	 */
	public Long getPostedByUser() {
		return postedByUser;
	}

	/**
	 * Sets the ID of the platform user who posted this property.
	 *
	 * @param postedByUser the poster's user ID
	 */
	public void setPostedByUser(Long postedByUser) {
		this.postedByUser = postedByUser;
	}

	/**
	 * Returns the date and time the listing was posted.
	 *
	 * @return the post date-time
	 */
	public LocalDateTime getPostDate() {
		return postDate;
	}

	/**
	 * Sets the date and time the listing was posted.
	 *
	 * @param postDate the post date-time
	 */
	public void setPostDate(LocalDateTime postDate) {
		this.postDate = postDate;
	}

	/**
	 * Returns whether this is a rental or sale listing.
	 *
	 * @return the rent-or-sale indicator
	 */
	public String getRentOrSale() {
		return rentOrSale;
	}

	/**
	 * Sets the rent-or-sale indicator.
	 *
	 * @param rentOrSale the rent-or-sale indicator
	 */
	public void setRentOrSale(String rentOrSale) {
		this.rentOrSale = rentOrSale;
	}

	/**
	 * Returns whether this listing has been verified.
	 *
	 * @return {@code true} if verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * Sets the verification flag.
	 *
	 * @param verified {@code true} to mark as verified
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * Returns a nearby landmark.
	 *
	 * @return the landmark
	 */
	public String getLandmark() {
		return landmark;
	}

	/**
	 * Sets a nearby landmark.
	 *
	 * @param landmark the landmark
	 */
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	/**
	 * Returns the geographic latitude.
	 *
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the geographic latitude.
	 *
	 * @param latitude the latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the geographic longitude.
	 *
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the geographic longitude.
	 *
	 * @param longitude the longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Returns the floor number on which the unit is located.
	 *
	 * @return the floor number
	 */
	public Integer getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Sets the floor number on which the unit is located.
	 *
	 * @param floorNumber the floor number
	 */
	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	/**
	 * Returns the total number of floors in the building.
	 *
	 * @return the total floors
	 */
	public Integer getTotalFloors() {
		return totalFloors;
	}

	/**
	 * Sets the total number of floors in the building.
	 *
	 * @param totalFloors the total floors
	 */
	public void setTotalFloors(Integer totalFloors) {
		this.totalFloors = totalFloors;
	}

	/**
	 * Returns the number of parking spots.
	 *
	 * @return the parking count
	 */
	public String getParkingCount() {
		return parkingCount;
	}

	/**
	 * Sets the number of parking spots.
	 *
	 * @param parkingCount the parking count
	 */
	public void setParkingCount(String parkingCount) {
		this.parkingCount = parkingCount;
	}

	/**
	 * Returns the parking type (e.g., Covered, Open, Both).
	 *
	 * @return the parking type
	 */
	public String getParkingType() {
		return parkingType;
	}

	/**
	 * Sets the parking type.
	 *
	 * @param parkingType the parking type
	 */
	public void setParkingType(String parkingType) {
		this.parkingType = parkingType;
	}

	/**
	 * Returns the direction the main entrance faces.
	 *
	 * @return the facing direction
	 */
	public String getFacing() {
		return facing;
	}

	/**
	 * Sets the direction the main entrance faces.
	 *
	 * @param facing the facing direction
	 */
	public void setFacing(String facing) {
		this.facing = facing;
	}

	/**
	 * Returns the age of the property in years.
	 *
	 * @return the property age
	 */
	public String getPropertyAge() {
		return propertyAge;
	}

	/**
	 * Sets the age of the property in years.
	 *
	 * @param propertyAge the property age
	 */
	public void setPropertyAge(String propertyAge) {
		this.propertyAge = propertyAge;
	}

	/**
	 * Returns the ownership type (e.g., Freehold, Leasehold).
	 *
	 * @return the ownership type
	 */
	public String getOwnershipType() {
		return ownershipType;
	}

	/**
	 * Sets the ownership type.
	 *
	 * @param ownershipType the ownership type
	 */
	public void setOwnershipType(String ownershipType) {
		this.ownershipType = ownershipType;
	}

	/**
	 * Returns whether the price is negotiable.
	 *
	 * @return {@code true} if negotiable
	 */
	public Boolean getNegotiable() {
		return negotiable;
	}

	/**
	 * Sets whether the price is negotiable.
	 *
	 * @param negotiable {@code true} if negotiable
	 */
	public void setNegotiable(Boolean negotiable) {
		this.negotiable = negotiable;
	}

	/**
	 * Returns whether a home loan is available.
	 *
	 * @return {@code true} if a loan is available
	 */
	public Boolean getLoanAvailable() {
		return loanAvailable;
	}

	/**
	 * Sets whether a home loan is available.
	 *
	 * @param loanAvailable {@code true} if a loan is available
	 */
	public void setLoanAvailable(Boolean loanAvailable) {
		this.loanAvailable = loanAvailable;
	}

	/**
	 * Returns the monthly rent amount.
	 *
	 * @return the monthly rent
	 */
	public Double getMonthlyRent() {
		return monthlyRent;
	}

	/**
	 * Sets the monthly rent amount.
	 *
	 * @param monthlyRent the monthly rent
	 */
	public void setMonthlyRent(Double monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	/**
	 * Returns the security deposit required.
	 *
	 * @return the security deposit
	 */
	public Double getSecurityDeposit() {
		return securityDeposit;
	}

	/**
	 * Sets the security deposit required.
	 *
	 * @param securityDeposit the security deposit
	 */
	public void setSecurityDeposit(Double securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	/**
	 * Returns the brokerage fee.
	 *
	 * @return the brokerage fee
	 */
	public Double getBrokerage() {
		return brokerage;
	}

	/**
	 * Sets the brokerage fee.
	 *
	 * @param brokerage the brokerage fee
	 */
	public void setBrokerage(Double brokerage) {
		this.brokerage = brokerage;
	}

	/**
	 * Returns the preferred tenant type (e.g., Family, Bachelor).
	 *
	 * @return the preferred tenants
	 */
	public String getPreferredTenants() {
		return preferredTenants;
	}

	/**
	 * Sets the preferred tenant type.
	 *
	 * @param preferredTenants the preferred tenants
	 */
	public void setPreferredTenants(String preferredTenants) {
		this.preferredTenants = preferredTenants;
	}

	/**
	 * Returns whether pets are allowed.
	 *
	 * @return {@code true} if pets are allowed
	 */
	public Boolean getPetsAllowed() {
		return petsAllowed;
	}

	/**
	 * Sets whether pets are allowed.
	 *
	 * @param petsAllowed {@code true} if pets are allowed
	 */
	public void setPetsAllowed(Boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}

	/**
	 * Returns whether non-vegetarian food is permitted.
	 *
	 * @return {@code true} if non-veg is allowed
	 */
	public Boolean getNonVegAllowed() {
		return nonVegAllowed;
	}

	/**
	 * Sets whether non-vegetarian food is permitted.
	 *
	 * @param nonVegAllowed {@code true} if non-veg is allowed
	 */
	public void setNonVegAllowed(Boolean nonVegAllowed) {
		this.nonVegAllowed = nonVegAllowed;
	}

	/**
	 * Returns the lease duration (e.g., "11 months", "1 year").
	 *
	 * @return the lease duration
	 */
	public String getLeaseDuration() {
		return leaseDuration;
	}

	/**
	 * Sets the lease duration.
	 *
	 * @param leaseDuration the lease duration
	 */
	public void setLeaseDuration(String leaseDuration) {
		this.leaseDuration = leaseDuration;
	}

	/**
	 * Returns the notice period required before vacating.
	 *
	 * @return the notice period
	 */
	public String getNoticePeriod() {
		return noticePeriod;
	}

	/**
	 * Sets the notice period required before vacating.
	 *
	 * @param noticePeriod the notice period
	 */
	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	/**
	 * Returns whether maintenance charges are included in the rent.
	 *
	 * @return {@code true} if maintenance is included
	 */
	public Boolean getMaintenanceIncluded() {
		return maintenanceIncluded;
	}

	/**
	 * Sets whether maintenance charges are included in the rent.
	 *
	 * @param maintenanceIncluded {@code true} if maintenance is included
	 */
	public void setMaintenanceIncluded(Boolean maintenanceIncluded) {
		this.maintenanceIncluded = maintenanceIncluded;
	}

	/**
	 * Returns the builder or developer name.
	 *
	 * @return the builder name
	 */
	public String getBuilderName() {
		return builderName;
	}

	/**
	 * Sets the builder or developer name.
	 *
	 * @param builderName the builder name
	 */
	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	/**
	 * Returns whether the project has RERA approval.
	 *
	 * @return {@code true} if RERA approved
	 */
	public Boolean getReraApproved() {
		return reraApproved;
	}

	/**
	 * Sets whether the project has RERA approval.
	 *
	 * @param reraApproved {@code true} if RERA approved
	 */
	public void setReraApproved(Boolean reraApproved) {
		this.reraApproved = reraApproved;
	}

	/**
	 * Returns the RERA registration number.
	 *
	 * @return the RERA number
	 */
	public String getReraNumber() {
		return reraNumber;
	}

	/**
	 * Sets the RERA registration number.
	 *
	 * @param reraNumber the RERA number
	 */
	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}

	/**
	 * Returns the number of times this listing has been viewed.
	 *
	 * @return the views count
	 */
	public Integer getViewsCount() {
		return viewsCount;
	}

	/**
	 * Sets the number of times this listing has been viewed.
	 *
	 * @param viewsCount the views count
	 */
	public void setViewsCount(Integer viewsCount) {
		this.viewsCount = viewsCount;
	}

	/**
	 * Returns the number of times this listing has been shortlisted.
	 *
	 * @return the shortlist count
	 */
	public Integer getShortListCount() {
		return shortListCount;
	}

	/**
	 * Sets the number of times this listing has been shortlisted.
	 *
	 * @param shortListCount the shortlist count
	 */
	public void setShortListCount(Integer shortListCount) {
		this.shortListCount = shortListCount;
	}

	// ================= GETTERS & SETTERS =================
	// (Generate using Lombok or IDE)

}
