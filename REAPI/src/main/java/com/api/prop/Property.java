package com.api.prop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.userproperty.UserPropertyRelation;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a real estate property listing.
 *
 * <p>Extends {@link BaseEntity} to inherit audit fields (code, createdTs,
 * lastUpdatedTs, createdBy, updatedBy) populated via JPA auditing. Covers all
 * aspects of a listing including basic info, pricing, area, location, building
 * details, rent-specific rules, builder/project data, and system metrics.</p>
 *
 * <p>Amenities are stored as a comma-separated string of integer IDs (e.g.
 * {@code "1,2,3"}) and can be converted to/from a {@code List<Integer>} via
 * the helper methods {@link #setAmenitiesFromList(List)} and
 * {@link #getAmenitiesAsList()}.</p>
 */
@Entity

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "property")
public class Property extends BaseEntity {



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Address is required")	private String address;

	@NotBlank(message = "City is required")
	private String city;
	@Enumerated(EnumType.STRING)
	private MasterEnums.PropertyStatusEnum propertyStatus;

	@NotBlank(message = "State is required")
	private String state;

	@NotBlank(message = "Type is required (e.g., Apartment, Villa, Plot)")
	private String type;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private Double price;

	@Min(value = 0, message = "Bedrooms cannot be negative")
	private Integer bedrooms;

	@Min(value = 0, message = "Bathrooms cannot be negative")
	private Integer bathrooms;

	@NotBlank(message = "Location is required (e.g., Sector 45, MG Road)")
	private String location;

	private Double carpetArea; // in square feet

	private Double superArea; // in square feet

	@Column(length = 255)
	private String amenities; // e.g., "1,2,3"

	@NotBlank(message = "PostedBy is required (e.g., Owner, Broker, Builder)")
	private String postedBy;
	private String contactNumber;

	private String constructionStatus;

	@Column(nullable = false)
	@Builder.Default
	private String currency = "INR";

	private LocalDate readyDate;

	@NotBlank(message = "Category is required (e.g., Residential, Commercial, Agricultural)")
	private String category;


	private String projectName;

	@Column(length = 2000)
	private String description;

	@Column(name = "posted_by_user_id")
	private Long postedByUser; // The user who posted the property

	@Builder.Default
	private LocalDateTime postDate = LocalDateTime.now(); // Auto-set when created

	@NotBlank(message = "Rent/Sale status is required (e.g., Rent or Sale)")
	private String rentOrSale; // Rent or Sale

	private boolean verified;

	private String landmark;
	private Double latitude;
	private Double longitude;

	private Integer floorNumber;
	private Integer totalFloors;

	private String parkingCount;
	private String parkingType; // Covered, Open, Both

	private String facing; // East, West, North, South

	private String propertyAge; // in years
	private String ownershipType; // Freehold, Leasehold

	private String furnishing;
	private Boolean negotiable;
	private Boolean loanAvailable;

	private Double monthlyRent;
	private Double securityDeposit;
	private Double brokerage;

	private String preferredTenants; // Family, Bachelor, Company

	private Boolean petsAllowed;
	private Boolean nonVegAllowed;

	private String leaseDuration; // 11 months, 1 year
	private String noticePeriod; // 1 month, 2 months

	private Boolean maintenanceIncluded;
	private String builderName;
	private Boolean reraApproved;
	private String reraNumber;


	private Integer viewsCount;
	private Integer shortListCount;

	@OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
//	@JsonManagedReference(value = "property-userrelation")
	@JsonIgnore
	private List<UserPropertyRelation> userRelations = new ArrayList<>();

	// --- Helper methods for amenities ---

	/**
	 * Sets the {@code amenities} field from a list of amenity IDs.
	 *
	 * <p>The IDs are joined as a comma-separated string (e.g. {@code "1,2,3"}).
	 * If the list is {@code null} or empty the field is set to {@code null}.</p>
	 *
	 * @param amenityIds list of integer amenity IDs; may be {@code null} or empty
	 */
	public void setAmenitiesFromList(List<Integer> amenityIds) {
		if (amenityIds != null && !amenityIds.isEmpty()) {
			this.amenities = String.join(",", amenityIds.stream().map(String::valueOf).toList());
		} else {
			this.amenities = null;
		}
	}

	/**
	 * Returns the amenities as a list of integer IDs parsed from the stored
	 * comma-separated string.
	 *
	 * @return list of amenity IDs, or an empty list if no amenities are set
	 */
	public List<Integer> getAmenitiesAsList() {
		if (this.amenities == null || this.amenities.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		return java.util.Arrays.stream(this.amenities.split(",")).map(Integer::valueOf).toList();
	}

	/**
	 * Returns the primary key of this property.
	 *
	 * @return the property ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the primary key of this property.
	 *
	 * @param id the property ID
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
	 * Returns the city where the property is located.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city where the property is located.
	 *
	 * @param city the city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the state where the property is located.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state where the property is located.
	 *
	 * @param state the state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the property type (e.g., Apartment, Villa, Plot).
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the property type (e.g., Apartment, Villa, Plot).
	 *
	 * @param type the type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the listed price of the property.
	 *
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * Sets the listed price of the property.
	 *
	 * @param price the price; must be greater than 0
	 */
	public void setPrice(Double price) {
		this.price = price;
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
	 * Returns the locality or micro-location within the city (e.g., Sector 45, MG Road).
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the locality or micro-location within the city.
	 *
	 * @param location the location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the raw comma-separated amenity ID string (e.g., {@code "1,2,3"}).
	 *
	 * @return the amenities string
	 */
	public String getAmenities() {
		return amenities;
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
	 * Sets who posted the listing (e.g., Owner, Broker, Builder).
	 *
	 * @param postedBy the postedBy value
	 */
	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	/**
	 * Returns the construction status (e.g., Under Construction, Ready to Move).
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
	 * Returns the property category (e.g., Residential, Commercial, Agricultural).
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
	 * Returns the detailed description of the property.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the detailed description of the property.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the ID of the platform user who posted this property.
	 *
	 * @return the user ID of the poster
	 */
	public Long getPostedByUser() {
		return postedByUser;
	}

	/**
	 * Sets the ID of the platform user who posted this property.
	 *
	 * @param postedByUser the user ID of the poster
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
	 * Returns whether this is a rental or a sale listing (e.g., {@code "Rent"} or {@code "Sale"}).
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
	 * Returns all user-property interactions (favourites, inquiries) linked to this property.
	 *
	 * @return the list of user-property relations
	 */
	public List<UserPropertyRelation> getUserRelations() {
		return userRelations;
	}

	/**
	 * Sets the list of user-property interactions for this property.
	 *
	 * @param userRelations the user-property relations
	 */
	public void setUserRelations(List<UserPropertyRelation> userRelations) {
		this.userRelations = userRelations;
	}

	/**
	 * Returns the carpet area of the property in square feet.
	 *
	 * @return the carpet area
	 */
	public Double getCarpetArea() {
		return carpetArea;
	}

	/**
	 * Sets the carpet area of the property in square feet.
	 *
	 * @param carpetArea the carpet area
	 */
	public void setCarpetArea(Double carpetArea) {
		this.carpetArea = carpetArea;
	}

	/**
	 * Returns the super built-up area of the property in square feet.
	 *
	 * @return the super area
	 */
	public Double getSuperArea() {
		return superArea;
	}

	/**
	 * Sets the super built-up area of the property in square feet.
	 *
	 * @param superArea the super area
	 */
	public void setSuperArea(Double superArea) {
		this.superArea = superArea;
	}

	/**
	 * Returns the current listing status of the property.
	 *
	 * @return the property status enum value
	 */
	public MasterEnums.PropertyStatusEnum getPropertyStatus() {
		return propertyStatus;
	}

	/**
	 * Sets the current listing status of the property.
	 *
	 * @param propertyStatus the property status enum value
	 */
	public void setPropertyStatus(MasterEnums.PropertyStatusEnum propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	/**
	 * Returns whether this listing has been verified by an admin.
	 *
	 * @return {@code true} if verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * Sets the verification flag for this listing.
	 *
	 * @param verified {@code true} to mark as verified
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * Returns the contact number for enquiries about this property.
	 *
	 * @return the contact number
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * Sets the contact number for enquiries about this property.
	 *
	 * @param contactNumber the contact number
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * Returns a nearby landmark for easier navigation.
	 *
	 * @return the landmark
	 */
	public String getLandmark() {
		return landmark;
	}

	/**
	 * Sets a nearby landmark for easier navigation.
	 *
	 * @param landmark the landmark
	 */
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	/**
	 * Returns the geographic latitude of the property.
	 *
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the geographic latitude of the property.
	 *
	 * @param latitude the latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the geographic longitude of the property.
	 *
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the geographic longitude of the property.
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
	 * Returns the number of parking spots associated with the property.
	 *
	 * @return the parking count
	 */
	public String getParkingCount() {
		return parkingCount;
	}

	/**
	 * Sets the number of parking spots associated with the property.
	 *
	 * @param parkingCount the parking count
	 */
	public void setParkingCount(String parkingCount) {
		this.parkingCount = parkingCount;
	}

	/**
	 * Returns the type of parking available (e.g., Covered, Open, Both).
	 *
	 * @return the parking type
	 */
	public String getParkingType() {
		return parkingType;
	}

	/**
	 * Sets the type of parking available.
	 *
	 * @param parkingType the parking type
	 */
	public void setParkingType(String parkingType) {
		this.parkingType = parkingType;
	}

	/**
	 * Returns the direction the main entrance faces (e.g., East, West, North, South).
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
	 * Sets the ownership type (e.g., Freehold, Leasehold).
	 *
	 * @param ownershipType the ownership type
	 */
	public void setOwnershipType(String ownershipType) {
		this.ownershipType = ownershipType;
	}

	/**
	 * Returns the furnishing status of the property (e.g., Furnished, Semi-Furnished, Unfurnished).
	 *
	 * @return the furnishing status
	 */
	public String getFurnishing() {
	    return furnishing;
	}

	/**
	 * Sets the furnishing status of the property.
	 *
	 * @param furnishing the furnishing status
	 */
	public void setFurnishing(String furnishing) {
	    this.furnishing = furnishing;
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
	 * Returns whether a home loan is available for this property.
	 *
	 * @return {@code true} if loan is available
	 */
	public Boolean getLoanAvailable() {
		return loanAvailable;
	}

	/**
	 * Sets whether a home loan is available for this property.
	 *
	 * @param loanAvailable {@code true} if loan is available
	 */
	public void setLoanAvailable(Boolean loanAvailable) {
		this.loanAvailable = loanAvailable;
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
	 * Returns the number of times this listing has been shortlisted by users.
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

	/**
	 * Returns the monthly rent amount (applicable for rental listings).
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
	 * Returns the security deposit required for this rental.
	 *
	 * @return the security deposit
	 */
	public Double getSecurityDeposit() {
		return securityDeposit;
	}

	/**
	 * Sets the security deposit required for this rental.
	 *
	 * @param securityDeposit the security deposit
	 */
	public void setSecurityDeposit(Double securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	/**
	 * Returns the brokerage fee applicable for this listing.
	 *
	 * @return the brokerage fee
	 */
	public Double getBrokerage() {
		return brokerage;
	}

	/**
	 * Sets the brokerage fee applicable for this listing.
	 *
	 * @param brokerage the brokerage fee
	 */
	public void setBrokerage(Double brokerage) {
		this.brokerage = brokerage;
	}

	/**
	 * Returns the preferred tenant type (e.g., Family, Bachelor, Company).
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
	 * Returns whether pets are allowed in this rental.
	 *
	 * @return {@code true} if pets are allowed
	 */
	public Boolean getPetsAllowed() {
		return petsAllowed;
	}

	/**
	 * Sets whether pets are allowed in this rental.
	 *
	 * @param petsAllowed {@code true} if pets are allowed
	 */
	public void setPetsAllowed(Boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}

	/**
	 * Returns whether non-vegetarian food is permitted on the premises.
	 *
	 * @return {@code true} if non-veg is allowed
	 */
	public Boolean getNonVegAllowed() {
		return nonVegAllowed;
	}

	/**
	 * Sets whether non-vegetarian food is permitted on the premises.
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
	 * Returns the notice period required before vacating (e.g., "1 month", "2 months").
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
	 * Returns the name of the builder or developer.
	 *
	 * @return the builder name
	 */
	public String getBuilderName() {
		return builderName;
	}

	/**
	 * Sets the name of the builder or developer.
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

}
