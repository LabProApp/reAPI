package com.api.projects;

import java.time.LocalDate;

import com.api.commons.BaseDto;

public class ProjectDto extends BaseDto{

    private Long id;
    private String projectName;
    private String builderName;
    private String city;
    private String state;
    private String location;

    private String reraNumber;
    private String projectStatus;

    private Double minPrice;
    private Double maxPrice;

    private String propertyType;

    private Double landArea;
    private Integer totalTowers;
    private Integer totalUnits;

    private Double rating;
    private LocalDate possessionDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getBuilderName() {
		return builderName;
	}
	public void setBuilderName(String builderName) {
		this.builderName = builderName;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getReraNumber() {
		return reraNumber;
	}
	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public Double getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}
	public Double getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public Double getLandArea() {
		return landArea;
	}
	public void setLandArea(Double landArea) {
		this.landArea = landArea;
	}
	public Integer getTotalTowers() {
		return totalTowers;
	}
	public void setTotalTowers(Integer totalTowers) {
		this.totalTowers = totalTowers;
	}
	public Integer getTotalUnits() {
		return totalUnits;
	}
	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public LocalDate getPossessionDate() {
		return possessionDate;
	}
	public void setPossessionDate(LocalDate possessionDate) {
		this.possessionDate = possessionDate;
	}
}
