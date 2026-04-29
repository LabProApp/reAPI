package com.api.commons;

import java.time.LocalDateTime;

/**
 * Abstract base class for all Data Transfer Objects (DTOs) in the Real Estate API.
 *
 * <p>Mirrors the auditing fields defined in {@link BaseEntity} so that entity
 * data can be mapped to and from DTOs without losing audit information.
 * Concrete DTO classes should extend this class and add their own domain-specific
 * fields.</p>
 */
public abstract class BaseDto {

	private String code;

	private LocalDateTime createdTs;

	private LocalDateTime lastUpdatedTs;

	private String createdBy;

	private String updatedBy;

	/** Returns the short unique code of the corresponding entity. */
	public String getCode() {
		return code;
	}

	/** Sets the short unique code of the corresponding entity. */
	public void setCode(String code) {
		this.code = code;
	}

	/** Returns the timestamp at which the corresponding entity was first persisted. */
	public LocalDateTime getCreatedTs() {
		return createdTs;
	}

	/** Sets the creation timestamp of the corresponding entity. */
	public void setCreatedTs(LocalDateTime createdTs) {
		this.createdTs = createdTs;
	}

	/** Returns the timestamp of the most recent update to the corresponding entity. */
	public LocalDateTime getLastUpdatedTs() {
		return lastUpdatedTs;
	}

	/** Sets the last-updated timestamp of the corresponding entity. */
	public void setLastUpdatedTs(LocalDateTime lastUpdatedTs) {
		this.lastUpdatedTs = lastUpdatedTs;
	}

	/** Returns the identifier of the principal who created the corresponding entity. */
	public String getCreatedBy() {
		return createdBy;
	}

	/** Sets the identifier of the principal who created the corresponding entity. */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/** Returns the identifier of the principal who last updated the corresponding entity. */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/** Sets the identifier of the principal who last updated the corresponding entity. */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
