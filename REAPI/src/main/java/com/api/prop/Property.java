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

	@NotBlank(message = "Address is required")
	private String address;

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
	@NotBlank(message = "Construction status is required (e.g., Ready to Move, Under Construction)")
	private String constructionStatus;

	@Column(nullable = false)
	@Builder.Default
	private String currency = "INR";

	private LocalDate readyDate;

	@NotBlank(message = "Category is required (e.g., Residential, Commercial, Agricultural)")
	private String category;

	@NotBlank(message = "Project name is required")
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

	private Integer parkingCount;
	private String parkingType; // Covered, Open, Both

	private String facing; // East, West, North, South

	private Integer propertyAge; // in years
	private String ownershipType; // Freehold, Leasehold

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
	public void setAmenitiesFromList(List<Integer> amenityIds) {
		if (amenityIds != null && !amenityIds.isEmpty()) {
			this.amenities = String.join(",", amenityIds.stream().map(String::valueOf).toList());
		} else {
			this.amenities = null;
		}
	}

	public List<Integer> getAmenitiesAsList() {
		if (this.amenities == null || this.amenities.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		return java.util.Arrays.stream(this.amenities.split(",")).map(Integer::valueOf).toList();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	public Integer getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms(Integer bathrooms) {
		this.bathrooms = bathrooms;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public String getConstructionStatus() {
		return constructionStatus;
	}

	public void setConstructionStatus(String constructionStatus) {
		this.constructionStatus = constructionStatus;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LocalDate getReadyDate() {
		return readyDate;
	}

	public void setReadyDate(LocalDate readyDate) {
		this.readyDate = readyDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPostedByUser() {
		return postedByUser;
	}

	public void setPostedByUser(Long postedByUser) {
		this.postedByUser = postedByUser;
	}

	public LocalDateTime getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDateTime postDate) {
		this.postDate = postDate;
	}

	public String getRentOrSale() {
		return rentOrSale;
	}

	public void setRentOrSale(String rentOrSale) {
		this.rentOrSale = rentOrSale;
	}

	public List<UserPropertyRelation> getUserRelations() {
		return userRelations;
	}

	public void setUserRelations(List<UserPropertyRelation> userRelations) {
		this.userRelations = userRelations;
	}

	public Double getCarpetArea() {
		return carpetArea;
	}

	public void setCarpetArea(Double carpetArea) {
		this.carpetArea = carpetArea;
	}

	public Double getSuperArea() {
		return superArea;
	}

	public void setSuperArea(Double superArea) {
		this.superArea = superArea;
	}

	public MasterEnums.PropertyStatusEnum getPropertyStatus() {
		return propertyStatus;
	}

	public void setPropertyStatus(MasterEnums.PropertyStatusEnum propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	public Integer getTotalFloors() {
		return totalFloors;
	}

	public void setTotalFloors(Integer totalFloors) {
		this.totalFloors = totalFloors;
	}

	public Integer getParkingCount() {
		return parkingCount;
	}

	public void setParkingCount(Integer parkingCount) {
		this.parkingCount = parkingCount;
	}

	public String getParkingType() {
		return parkingType;
	}

	public void setParkingType(String parkingType) {
		this.parkingType = parkingType;
	}

	public String getFacing() {
		return facing;
	}

	public void setFacing(String facing) {
		this.facing = facing;
	}

	public Integer getPropertyAge() {
		return propertyAge;
	}

	public void setPropertyAge(Integer propertyAge) {
		this.propertyAge = propertyAge;
	}

	public String getOwnershipType() {
		return ownershipType;
	}

	public void setOwnershipType(String ownershipType) {
		this.ownershipType = ownershipType;
	}

	public Boolean getNegotiable() {
		return negotiable;
	}

	public void setNegotiable(Boolean negotiable) {
		this.negotiable = negotiable;
	}

	public Boolean getLoanAvailable() {
		return loanAvailable;
	}

	public void setLoanAvailable(Boolean loanAvailable) {
		this.loanAvailable = loanAvailable;
	}

	public Integer getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(Integer viewsCount) {
		this.viewsCount = viewsCount;
	}

	public Integer getShortListCount() {
		return shortListCount;
	}

	public void setShortListCount(Integer shortListCount) {
		this.shortListCount = shortListCount;
	}

	public Double getMonthlyRent() {
		return monthlyRent;
	}

	public void setMonthlyRent(Double monthlyRent) {
		this.monthlyRent = monthlyRent;
	}

	public Double getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(Double securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public Double getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(Double brokerage) {
		this.brokerage = brokerage;
	}

	public String getPreferredTenants() {
		return preferredTenants;
	}

	public void setPreferredTenants(String preferredTenants) {
		this.preferredTenants = preferredTenants;
	}

	public Boolean getPetsAllowed() {
		return petsAllowed;
	}

	public void setPetsAllowed(Boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}

	public Boolean getNonVegAllowed() {
		return nonVegAllowed;
	}

	public void setNonVegAllowed(Boolean nonVegAllowed) {
		this.nonVegAllowed = nonVegAllowed;
	}

	public String getLeaseDuration() {
		return leaseDuration;
	}

	public void setLeaseDuration(String leaseDuration) {
		this.leaseDuration = leaseDuration;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public Boolean getMaintenanceIncluded() {
		return maintenanceIncluded;
	}

	public void setMaintenanceIncluded(Boolean maintenanceIncluded) {
		this.maintenanceIncluded = maintenanceIncluded;
	}

	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public Boolean getReraApproved() {
		return reraApproved;
	}

	public void setReraApproved(Boolean reraApproved) {
		this.reraApproved = reraApproved;
	}

	public String getReraNumber() {
		return reraNumber;
	}

	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}

}
