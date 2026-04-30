package com.api.commons;

import java.time.LocalDateTime;

public abstract class BaseDto {

	private String code;

	private LocalDateTime createdTs;

	private LocalDateTime lastUpdatedTs;

	private String createdBy;

	private String updatedBy;

	
	public String getCode() {
		return code;
	}

	
	public void setCode(String code) {
		this.code = code;
	}

	
	public LocalDateTime getCreatedTs() {
		return createdTs;
	}

	
	public void setCreatedTs(LocalDateTime createdTs) {
		this.createdTs = createdTs;
	}

	
	public LocalDateTime getLastUpdatedTs() {
		return lastUpdatedTs;
	}

	
	public void setLastUpdatedTs(LocalDateTime lastUpdatedTs) {
		this.lastUpdatedTs = lastUpdatedTs;
	}

	
	public String getCreatedBy() {
		return createdBy;
	}

	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	
	public String getUpdatedBy() {
		return updatedBy;
	}

	
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
