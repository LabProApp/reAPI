package com.api.documents;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor

@Table(name = "documents")
public class Documents extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// AWS S3 key
	@Column(nullable = false, length = 1000)
	private String s3key;

	@Column(nullable = false, length = 256)
	private String filename;

	// Formal document name e.g. "Aadhaar Card", "PAN Card", "Title Deed"
	@Column(length = 150)
	private String title;

	@Column(nullable = false, length = 20)
	private String docType;

	@Column(nullable = false, length = 50)
	private String objectType;

	@Column(nullable = false)
	private Long objectId;

	private String caption;

	// ✅ Document verification status
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MasterEnums.DocumentStatus documentStatus = MasterEnums.DocumentStatus.VERIFIED;

	// Optional: reason if rejected
	@Column(length = 500)
	private String rejectionReason;

	// Optional:comments
	@Column(length = 500)
	private String comments;
	/* Getters & Setters */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public MasterEnums.DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(MasterEnums.DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getS3key() {
		return s3key;
	}

	public void setS3key(String s3key) {
		this.s3key = s3key;
	}

}
