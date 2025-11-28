package com.api.documents;

import com.api.commons.BaseEntity;

public class DocumentDto extends BaseEntity {

	private Long id;
	private String docUrl;

	// IMAGE, VIDEO, PDF, DOC, etc.
	private String docType;

	// Generic category: USER, PROPERTY, etc.
	private String objectType;

	// Generic ID pointing to any table
	private Long objectId;

	private String caption;

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

}
