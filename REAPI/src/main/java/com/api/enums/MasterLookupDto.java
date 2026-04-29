package com.api.enums;

/**
 * Data Transfer Object for {@link MasterLookup} used in API responses.
 *
 * <p>Carries the essential fields of a lookup entry ({@code id}, {@code type},
 * {@code code}, and {@code value}) without exposing JPA-managed state or the
 * lazy-loaded {@code parent} association.
 */
public class MasterLookupDto {

	private Long id;

	private String type; // CITY / STATE / BANK etc.
	private String code;
	private String value;


	/**
	 * Constructs a {@code MasterLookupDto} with the three most-used fields.
	 *
	 * @param id    the surrogate identifier of the lookup entry
	 * @param value the human-readable display value
	 * @param type  the category type (e.g., {@code "CITY"}, {@code "STATE"})
	 */
	public MasterLookupDto(Long id, String value, String type) {
		this.id = id;
		this.value = value;
		this.type = type;
	}

	/** Returns the surrogate identifier of this lookup entry. */
	public Long getId() {
		return id;
	}

	/** Sets the surrogate identifier of this lookup entry. */
	public void setId(Long id) {
		this.id = id;
	}

	/** Returns the category type of this lookup entry (e.g., {@code "CITY"}, {@code "STATE"}). */
	public String getType() {
		return type;
	}

	/** Sets the category type of this lookup entry (e.g., {@code "CITY"}, {@code "STATE"}). */
	public void setType(String type) {
		this.type = type;
	}

	/** Returns the short code identifying this lookup value. */
	public String getCode() {
		return code;
	}

	/** Sets the short code identifying this lookup value. */
	public void setCode(String code) {
		this.code = code;
	}

	/** Returns the human-readable display value for this lookup entry. */
	public String getValue() {
		return value;
	}

	/** Sets the human-readable display value for this lookup entry. */
	public void setValue(String value) {
		this.value = value;
	}



	// Getters & Setters
}
