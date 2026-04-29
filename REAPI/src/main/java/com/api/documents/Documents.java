package com.api.documents;

import java.time.LocalDateTime;

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

/**
 * JPA entity representing a document stored in AWS S3 and tracked in the
 * {@code documents} database table.
 *
 * <p>A document is associated with a generic parent object via the
 * {@code objectType} / {@code objectId} pair (e.g. {@code "PROPERTY"},
 * {@code "LOAN_DOCUMENT"}, {@code "LEGAL_DOCUMENT"}, {@code "BANK"}).
 * Documents that require review (LOAN_DOCUMENT, LEGAL_DOCUMENT) default to
 * {@link MasterEnums.DocumentStatus#NOT_VERIFIED}; all others default to
 * {@link MasterEnums.DocumentStatus#VERIFIED}.
 */
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

	// User who uploaded the document
	@Column(name = "uploaded_by")
	private Long uploadedBy;

	// Admin/agent who reviewed (verified/rejected) the document
	@Column(name = "reviewed_by")
	private Long reviewedBy;

	@Column(name = "reviewed_ts")
	private LocalDateTime reviewedTs;

	/* Getters & Setters */

	/**
	 * Returns the primary-key identifier of this document.
	 *
	 * @return the document id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the primary-key identifier of this document.
	 *
	 * @param id the document id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the original (sanitised) filename as stored in S3.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the original (sanitised) filename.
	 *
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns the human-readable title of the document (e.g. "Aadhaar Card",
	 * "Title Deed").
	 *
	 * @return the document title, or {@code null} if not set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the human-readable title of the document.
	 *
	 * @param title the document title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the detected document type (e.g. {@code "IMAGE"}, {@code "PDF"},
	 * {@code "DOC"}, {@code "EXCEL"}).
	 *
	 * @return the doc type string
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the detected document type.
	 *
	 * @param docType the doc type string
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Returns the category of the owning entity (e.g. {@code "PROPERTY"},
	 * {@code "LOAN_DOCUMENT"}, {@code "LEGAL_DOCUMENT"}, {@code "BANK"}).
	 *
	 * @return the object type
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Sets the category of the owning entity.
	 *
	 * @param objectType the object type
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Returns the primary-key of the owning entity identified by
	 * {@link #getObjectType()}.
	 *
	 * @return the object id
	 */
	public Long getObjectId() {
		return objectId;
	}

	/**
	 * Sets the primary-key of the owning entity.
	 *
	 * @param objectId the object id
	 */
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	/**
	 * Returns an optional caption or short description for this document.
	 *
	 * @return the caption, or {@code null} if not set
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets an optional caption or short description for this document.
	 *
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Returns the reason for rejection when the document status is
	 * {@link MasterEnums.DocumentStatus#REJECTED}.
	 *
	 * @return the rejection reason, or {@code null} if not rejected
	 */
	public String getRejectionReason() {
		return rejectionReason;
	}

	/**
	 * Sets the reason for rejection.
	 *
	 * @param rejectionReason the rejection reason
	 */
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	/**
	 * Returns the current verification status of this document.
	 *
	 * @return the document status
	 */
	public MasterEnums.DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	/**
	 * Sets the verification status of this document.
	 *
	 * @param documentStatus the document status to set
	 */
	public void setDocumentStatus(MasterEnums.DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	/**
	 * Returns reviewer comments associated with the status decision.
	 *
	 * @return the comments, or {@code null} if not set
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets reviewer comments associated with the status decision.
	 *
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Returns the AWS S3 object key used to retrieve or delete the file.
	 *
	 * @return the S3 key
	 */
	public String getS3key() {
		return s3key;
	}

	/**
	 * Sets the AWS S3 object key.
	 *
	 * @param s3key the S3 key to set
	 */
	public void setS3key(String s3key) {
		this.s3key = s3key;
	}

	/** @return the id of the user who uploaded this document */
	public Long getUploadedBy() { return uploadedBy; }

	/**
	 * Sets the id of the user who uploaded this document.
	 *
	 * @param uploadedBy the uploader's user id
	 */
	public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

	/** @return the id of the admin or agent who reviewed this document */
	public Long getReviewedBy() { return reviewedBy; }

	/**
	 * Sets the id of the admin or agent who reviewed this document.
	 *
	 * @param reviewedBy the reviewer's user id
	 */
	public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }

	/** @return the timestamp at which the review decision was made */
	public LocalDateTime getReviewedTs() { return reviewedTs; }

	/**
	 * Sets the timestamp at which the review decision was made.
	 *
	 * @param reviewedTs the review timestamp
	 */
	public void setReviewedTs(LocalDateTime reviewedTs) { this.reviewedTs = reviewedTs; }

}
