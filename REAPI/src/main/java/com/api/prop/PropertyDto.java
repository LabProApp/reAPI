package com.api.prop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.api.commons.BaseDto;
import com.api.documents.DocumentminDto;
import com.api.enums.MasterEnums;
import com.api.userproperty.UserPropertyRelation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

	@Enumerated(EnumType.STRING)
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

	private Integer parkingCount;
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
	// ================= RELATIONS =================


	@JsonIgnore
	private List<UserPropertyRelation> userRelations = new ArrayList<>();

	// ================= AMENITIES HELPERS =================

	public void setAmenitiesFromList(List<Integer> amenityIds) {
		if (amenityIds != null && !amenityIds.isEmpty()) {
			this.amenities = amenityIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		} else {
			this.amenities = null;
		}
	}

	public List<Integer> getAmenitiesAsList() {
		if (this.amenities == null || this.amenities.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(this.amenities.split(",")).map(Integer::valueOf).collect(Collectors.toList());
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

	public MasterEnums.PropertyStatusEnum getPropertyStatus() {
		return propertyStatus;
	}

	public void setPropertyStatus(MasterEnums.PropertyStatusEnum propertyStatus) {
		this.propertyStatus = propertyStatus;
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

	public List<DocumentminDto> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<DocumentminDto> documentList) {
		this.documentList = documentList;
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

	public String getAmenities() {
		return amenities;
	}
	public String getFurnishing() {
	    return furnishing;
	}

	public void setFurnishing(String furnishing) {
	    this.furnishing = furnishing;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
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

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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

	public String getPropertyAge() {
		return propertyAge;
	}

	public void setPropertyAge(String propertyAge) {
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

	public List<UserPropertyRelation> getUserRelations() {
		return userRelations;
	}

	public void setUserRelations(List<UserPropertyRelation> userRelations) {
		this.userRelations = userRelations;
	}

	// ================= GETTERS & SETTERS =================
	// (Generate using Lombok or IDE)

}