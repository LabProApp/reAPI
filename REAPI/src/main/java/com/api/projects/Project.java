package com.api.projects;

import java.time.LocalDate;

import com.api.commons.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a real estate development project.
 *
 * <p>Stores core project details such as builder information, location,
 * RERA registration, pricing, unit counts, property type, and possession date.
 * Mapped to the {@code projects} database table and extends {@link BaseEntity}
 * for audit fields.</p>
 */
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String projectName;
	private String builderName;
	private String city;
	private String state;
	private String location;

	private String reraNumber;
	private String projectStatus; // Completed, Under Construction, Upcoming

	private Double minPrice;
	private Double maxPrice;

	private String propertyType; // Residential, Commercial, Mixed

	private Double landArea;
	private Integer totalTowers;
	private Integer totalUnits;

	private Double rating;

	private LocalDate possessionDate;

	/**
	 * Returns the unique identifier of this project.
	 *
	 * @return the project ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this project.
	 *
	 * @param id the project ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the project.
	 *
	 * @return the project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the name of the project.
	 *
	 * @param projectName the project name to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Returns the name of the builder/developer for this project.
	 *
	 * @return the builder name
	 */
	public String getBuilderName() {
		return builderName;
	}

	/**
	 * Sets the name of the builder/developer for this project.
	 *
	 * @param builderName the builder name to set
	 */
	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	/**
	 * Returns the city where the project is located.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city where the project is located.
	 *
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the state where the project is located.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state where the project is located.
	 *
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the specific locality or address of the project.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the specific locality or address of the project.
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the RERA (Real Estate Regulatory Authority) registration number.
	 *
	 * @return the RERA number
	 */
	public String getReraNumber() {
		return reraNumber;
	}

	/**
	 * Sets the RERA registration number for this project.
	 *
	 * @param reraNumber the RERA number to set
	 */
	public void setReraNumber(String reraNumber) {
		this.reraNumber = reraNumber;
	}

	/**
	 * Returns the current project status (e.g., Completed, Under Construction, Upcoming).
	 *
	 * @return the project status
	 */
	public String getProjectStatus() {
		return projectStatus;
	}

	/**
	 * Sets the current project status (e.g., Completed, Under Construction, Upcoming).
	 *
	 * @param projectStatus the project status to set
	 */
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	/**
	 * Returns the minimum price of units in this project.
	 *
	 * @return the minimum price
	 */
	public Double getMinPrice() {
		return minPrice;
	}

	/**
	 * Sets the minimum price of units in this project.
	 *
	 * @param minPrice the minimum price to set
	 */
	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	/**
	 * Returns the maximum price of units in this project.
	 *
	 * @return the maximum price
	 */
	public Double getMaxPrice() {
		return maxPrice;
	}

	/**
	 * Sets the maximum price of units in this project.
	 *
	 * @param maxPrice the maximum price to set
	 */
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	/**
	 * Returns the property type (e.g., Residential, Commercial, Mixed).
	 *
	 * @return the property type
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * Sets the property type (e.g., Residential, Commercial, Mixed).
	 *
	 * @param propertyType the property type to set
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * Returns the total land area of the project in applicable units.
	 *
	 * @return the land area
	 */
	public Double getLandArea() {
		return landArea;
	}

	/**
	 * Sets the total land area of the project.
	 *
	 * @param landArea the land area to set
	 */
	public void setLandArea(Double landArea) {
		this.landArea = landArea;
	}

	/**
	 * Returns the total number of towers in the project.
	 *
	 * @return the total number of towers
	 */
	public Integer getTotalTowers() {
		return totalTowers;
	}

	/**
	 * Sets the total number of towers in the project.
	 *
	 * @param totalTowers the total tower count to set
	 */
	public void setTotalTowers(Integer totalTowers) {
		this.totalTowers = totalTowers;
	}

	/**
	 * Returns the total number of units in the project.
	 *
	 * @return the total unit count
	 */
	public Integer getTotalUnits() {
		return totalUnits;
	}

	/**
	 * Sets the total number of units in the project.
	 *
	 * @param totalUnits the total unit count to set
	 */
	public void setTotalUnits(Integer totalUnits) {
		this.totalUnits = totalUnits;
	}

	/**
	 * Returns the average user/expert rating for this project.
	 *
	 * @return the rating
	 */
	public Double getRating() {
		return rating;
	}

	/**
	 * Sets the average user/expert rating for this project.
	 *
	 * @param rating the rating to set
	 */
	public void setRating(Double rating) {
		this.rating = rating;
	}

	/**
	 * Returns the expected or actual possession date for units in this project.
	 *
	 * @return the possession date
	 */
	public LocalDate getPossessionDate() {
		return possessionDate;
	}

	/**
	 * Sets the expected or actual possession date for units in this project.
	 *
	 * @param possessionDate the possession date to set
	 */
	public void setPossessionDate(LocalDate possessionDate) {
		this.possessionDate = possessionDate;
	}

}
