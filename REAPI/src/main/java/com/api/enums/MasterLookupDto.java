package com.api.enums;

public class MasterLookupDto {

	private Long id;

	private String type; // CITY / STATE / BANK etc.
	private String code;
	private String value;
	private String status;

	private MasterLookupDto parent; // State for which this City belongs

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MasterLookupDto getParent() {
		return parent;
	}

	public void setParent(MasterLookupDto parent) {
		this.parent = parent;
	}

	// Getters & Setters
}
