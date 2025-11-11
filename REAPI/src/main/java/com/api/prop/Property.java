package com.api.prop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api.user.User;
import com.api.userproperty.UserPropertyRelation;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "City is required")
	private String city;

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

	@NotNull(message = "Area is required")
	@Positive(message = "Area must be greater than 0")
	private Double area; // in square feet

	@Column(length = 255)
	private String amenities; // e.g., "1,2,3"

	@NotBlank(message = "PostedBy is required (e.g., Owner, Broker, Builder)")
	private String postedBy;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posted_by_user_id")
	private User postedByUser; // The user who posted the property
	@Builder.Default
	private LocalDateTime postDate = LocalDateTime.now(); // Auto-set when created

	@NotBlank(message = "Rent/Sale status is required (e.g., Rent or Sale)")
	private String rentOrSale; // Rent or Sale

	@OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonManagedReference(value = "property-documents")
	private List<Documents> documents = new ArrayList<>();

	@OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonManagedReference(value = "property-userrelation")
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

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
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

	public User getPostedByUser() {
		return postedByUser;
	}

	public void setPostedByUser(User postedByUser) {
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

	public List<Documents> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
	}
}
