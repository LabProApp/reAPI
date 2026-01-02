package com.api.prop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api.commons.BaseDto;
import com.api.documents.DocumentDto;
import com.api.documents.DocumentminDto;
import com.api.enums.MasterEnums;

public class PropertyDto extends BaseDto {

	private Long id;

	private String title;
	private String address;
	private String city;
	private MasterEnums.PropertyStatusEnum propertyStatus;
	private MasterEnums.PackageEnum planPackage;
	private String state;
	private String type;
	private Double price;
	private Integer bedrooms;
	private Integer bathrooms;
	private String location;
	private Double carpetArea; // in square feet
	private Double superArea; // in square feet
	private String amenities; // e.g., "1,2,3"
	private String postedBy;
	private String constructionStatus;
	private String currency;
	private String contactNumber;
	private LocalDate readyDate;
	private String category;
	private String projectName;
	private String description;
	private Long postedByUser;
	private LocalDateTime postDate;
	private String rentOrSale; // Rent or Sale
	private boolean verified;
	// private List<UserPropertyRelationDto> userRelations = new ArrayList<>();

	private List<DocumentminDto> documentList;

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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
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

	/*
	 * public List<UserPropertyRelationDto> getUserRelations() { return
	 * userRelations; }
	 * 
	 * public void setUserRelations(List<UserPropertyRelationDto> userRelations) {
	 * this.userRelations = userRelations; }
	 */

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

	public MasterEnums.PackageEnum getPlanPackage() {
		return planPackage;
	}

	public void setPlanPackage(MasterEnums.PackageEnum planPackage) {
		this.planPackage = planPackage;
	}

	public List<DocumentminDto> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<DocumentminDto> documentList) {
		this.documentList = documentList;
	}
}
