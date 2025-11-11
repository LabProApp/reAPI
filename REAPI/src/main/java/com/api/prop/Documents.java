package com.api.prop;

import java.time.LocalDateTime;

import com.api.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documents {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// AWS S3 URL for the uploaded file
	@Column(nullable = false, length = 1000)
	private String docUrl;

	// IMAGE or VIDEO
	@Column(nullable = false, length = 20)
	private String docType;

	// PROPERTY or USER (to identify what this doc belongs to)
	@Column(nullable = false, length = 20)
	private String docCategory; // e.g. PROPERTY, USER

	private String caption;
	@Builder.Default
	private LocalDateTime uploadedAt = LocalDateTime.now();

	// If this doc belongs to a Property

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference(value = "property-documents")
	@JoinColumn(name = "property_id", nullable = true)
	private Property property;

	// If this doc belongs to a User

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference(value = "user-documents")
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
