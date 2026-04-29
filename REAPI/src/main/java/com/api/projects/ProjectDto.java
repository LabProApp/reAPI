package com.api.projects;

import java.time.LocalDate;

import com.api.commons.BaseDto;

/**
 * Data Transfer Object for {@link Project}.
 *
 * <p>Used to transfer real estate project data between the API layer and clients,
 * mirroring the fields of the {@link Project} entity while decoupling the
 * persistence model from the API contract. Extends {@link BaseDto} for common
 * audit/metadata fields.</p>
 */
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

	/** @return the project ID */
	public Long getId() {
		return id;
	}

	/** @param id the project ID to set */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return the project name */
	public String getProjectName() {
		return projectName;
	}

	/** @param projectName the project name to set */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/** @return the builder name */
	public String getBuilderName() {
		return builderName;
	}

	/** @param builderName the builder name to set */
	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	/** @return the city */
	public String getCity() {
		return city;
	}

	/** @param city the city to set */
	public void setCity(String city) {
		this.city = city;
	}

	/** @return the state */
	public String getState() {
		return state;
	}

	/** @param state the state to set */
	public void setState(String state) {
		this.state = state;
	}

	/** @return the location/locality */
	public String getLocation() {
		return location;
	}

	/** @param location the location to set */
	public void setLocation(String location) {
		this.location = location;
	}

	/** @return the RERA registration number */
	public String getReraNumber() {
		return reraNumber;
	}

	/** @param reraNumber the RERA number to set */
	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}

	/** @return the project status (e.g., Completed, Under Construction, Upcoming) */
	public String getProjectStatus() {
		return projectStatus;
	}

	/** @param projectStatus the project status to set */
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	/** @return the minimum unit price */
	public Double getMinPrice() {
		return minPrice;
	}

	/** @param minPrice the minimum price to set */
	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	/** @return the maximum unit price */
	public Double getMaxPrice() {
		return maxPrice;
	}

	/** @param maxPrice the maximum price to set */
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	/** @return the property type (e.g., Residential, Commercial, Mixed) */
	public String getPropertyType() {
		return propertyType;
	}

	/** @param propertyType the property type to set */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	/** @return the total land area */
	public Double getLandArea() {
		return landArea;
	}

	/** @param landArea the land area to set */
	public void setLandArea(Double landArea) {
		this.landArea = landArea;
	}

	/** @return the total number of towers */
	public Integer getTotalTowers() {
		return totalTowers;
	}

	/** @param totalTowers the total tower count to set */
	public void setTotalTowers(Integer totalTowers) {
		this.totalTowers = totalTowers;
	}

	/** @return the total number of units */
	public Integer getTotalUnits() {
		return totalUnits;
	}

	/** @param totalUnits the total unit count to set */
	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

	/** @return the project rating */
	public Double getRating() {
		return rating;
	}

	/** @param rating the rating to set */
	public void setRating(Double rating) {
		this.rating = rating;
	}

	/** @return the possession date */
	public LocalDate getPossessionDate() {
		return possessionDate;
	}

	/** @param possessionDate the possession date to set */
	public void setPossessionDate(LocalDate possessionDate) {
		this.possessionDate = possessionDate;
	}
}
