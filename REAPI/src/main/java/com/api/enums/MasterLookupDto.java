package com.api.enums;

public class MasterLookupDto {

	private Long id;

	private String type; // CITY / STATE / BANK etc.
	private String code;
	private String value;
	

	public MasterLookupDto(Long id, String value, String type) {
		this.id = id;
		this.value = value;
		this.type = type;
	}
	
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

	

	// Getters & Setters
}
