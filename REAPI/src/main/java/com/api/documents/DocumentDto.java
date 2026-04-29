package com.api.documents;

import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

/**
 * Full data-transfer object representing all fields of the {@link Documents}
 * entity, including a presigned S3 download URL generated at query time.
 *
 * <p>This DTO is returned by the Documents API for detailed view and
 * admin/review endpoints. For lightweight list responses (property or bank
 * summaries) prefer {@link DocumentminDto} to reduce payload size.
 */
public class DocumentDto extends BaseDto {

	private Long id;
	private String docUrl;
	private String s3key;
	private String filename;
	private String title;
	// IMAGE, VIDEO, PDF, DOC, etc.
	private String docType;

	// Generic category: USER, PROPERTY, LOAN_DOCUMENT, LEGAL_DOCUMENT, etc.
	private String objectType;

	// Generic ID pointing to any table
	private Long objectId;

	private String caption;

	private MasterEnums.DocumentStatus documentStatus;
	private String rejectionReason;

	private String comments;

	private Long uploadedBy;
	private Long reviewedBy;
	private LocalDateTime reviewedTs;

	/** @return the document id */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the document id.
	 *
	 * @param id the document id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the 60-minute presigned S3 URL for downloading the document.
	 *
	 * @return the presigned download URL
	 */
	public String getDocUrl() {
		return docUrl;
	}

	/**
	 * Sets the presigned S3 download URL.
	 *
	 * @param docUrl the presigned URL
	 */
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}

	/** @return the AWS S3 object key */
	public String getS3key() {
		return s3key;
	}

	/**
	 * Sets the AWS S3 object key.
	 *
	 * @param s3key the S3 key
	 */
	public void setS3key(String s3key) {
		this.s3key = s3key;
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
	 * Returns the optional caption or short description of the document.
	 *
	 * @return the caption, or {@code null} if not set
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the optional caption or short description of the document.
	 *
	 * @param caption the caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/** @return the original (sanitised) filename */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the original (sanitised) filename.
	 *
	 * @param filename the filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns the human-readable title of the document (e.g. "Aadhaar Card").
	 *
	 * @return the title, or {@code null} if not set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the human-readable title of the document.
	 *
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the current verification status of the document.
	 *
	 * @return the document status
	 */
	public MasterEnums.DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	/**
	 * Sets the verification status of the document.
	 *
	 * @param documentStatus the document status
	 */
	public void setDocumentStatus(MasterEnums.DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	/**
	 * Returns the reason for rejection when status is
	 * {@link MasterEnums.DocumentStatus#REJECTED}.
	 *
	 * @return the rejection reason, or {@code null}
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
	 * Returns reviewer comments associated with the status decision.
	 *
	 * @return the comments, or {@code null}
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets reviewer comments associated with the status decision.
	 *
	 * @param comments the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/** @return the id of the user who uploaded the document */
	public Long getUploadedBy() { return uploadedBy; }

	/**
	 * Sets the id of the user who uploaded the document.
	 *
	 * @param uploadedBy the uploader's user id
	 */
	public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

	/** @return the id of the admin or agent who reviewed the document */
	public Long getReviewedBy() { return reviewedBy; }

	/**
	 * Sets the id of the admin or agent who reviewed the document.
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
