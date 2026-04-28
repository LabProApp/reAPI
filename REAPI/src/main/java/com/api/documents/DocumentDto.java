package com.api.documents;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

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

	public String getS3key() {
		return s3key;
	}

	public void setS3key(String s3key) {
		this.s3key = s3key;
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

	public MasterEnums.DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(MasterEnums.DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
