package com.api.documents;

import java.time.LocalDateTime;

import com.api.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documents extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// AWS S3 URL
	@Column(nullable = false, length = 1000)
	private String docUrl;

	// IMAGE, VIDEO, PDF, DOC, etc.
	@Column(nullable = false, length = 20)
	private String docType;

	// Generic category: USER, PROPERTY, etc.
	@Column(nullable = false, length = 50)
	private String objectType;

	// Generic ID pointing to any table
	@Column(nullable = false)
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
