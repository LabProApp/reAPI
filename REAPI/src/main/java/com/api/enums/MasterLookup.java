package com.api.enums;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a single entry in the {@code master_lookup} reference table.
 *
 * <p>The master lookup table backs database-driven drop-down and reference data used
 * throughout the platform (e.g., cities, states, banks). Each record has a {@code type}
 * discriminator (e.g., {@code "CITY"}, {@code "STATE"}, {@code "BANK"}), a short
 * {@code code}, a human-readable {@code value}, and a {@code status} flag.
 *
 * <p>Hierarchical data (e.g., a city belonging to a state) is modelled via the
 * {@code parent} self-referential {@link ManyToOne} association.
 */
@Entity
@Table(name = "master_lookup")
public class MasterLookup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type; // CITY / STATE / BANK etc.
	private String code;
	private String value;
	private String status;

	// Parent reference (for CITY → STATE relationship)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private MasterLookup parent; // State for which this City belongs

	/** Returns the unique surrogate identifier for this lookup entry. */
	public Long getId() {
		return id;
	}

	/** Sets the unique surrogate identifier for this lookup entry. */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the category type of this lookup entry (e.g., {@code "CITY"}, {@code "STATE"}).
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the category type of this lookup entry (e.g., {@code "CITY"}, {@code "STATE"}).
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** Returns the short code identifying this lookup value (e.g., an ISO code). */
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

	/** Returns the current status of this lookup entry (e.g., {@code "ACTIVE"}). */
	public String getStatus() {
		return status;
	}

	/** Sets the current status of this lookup entry (e.g., {@code "ACTIVE"}). */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns the parent lookup entry in a hierarchical relationship
	 * (e.g., the {@code STATE} entry that this {@code CITY} belongs to).
	 */
	public MasterLookup getParent() {
		return parent;
	}

	/**
	 * Sets the parent lookup entry for a hierarchical relationship
	 * (e.g., the {@code STATE} entry that this {@code CITY} belongs to).
	 */
	public void setParent(MasterLookup parent) {
		this.parent = parent;
	}

	// Getters & Setters
}
