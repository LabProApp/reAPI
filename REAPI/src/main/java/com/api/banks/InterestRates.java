package com.api.banks;


import com.api.commons.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a single CIBIL-score-range interest rate slab for a
 * bank partner. Each slab defines the minimum and maximum CIBIL scores that
 * qualify for the associated annual interest rate. The owning {@link Bank} is
 * loaded lazily and excluded from JSON serialisation via
 * {@link JsonBackReference} to prevent circular references.
 */
@Entity
@Table(name ="interest_rates")
public class InterestRates extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Minimum CIBIL score (inclusive) for this rate slab. */
    private int minCibil;

    /** Maximum CIBIL score (inclusive) for this rate slab. */
    private int maxCibil;

    /** Annual interest rate (percentage) applicable to this CIBIL range. */
    private double interestRate;

    /**
     * The bank to which this interest rate slab belongs. Loaded lazily and
     * suppressed during JSON serialisation to avoid back-reference cycles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonBackReference
    private Bank bank;

	/** @return the surrogate primary key of this rate slab */
	public Long getId() {
		return id;
	}

	/** @param id the surrogate primary key to set */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return the minimum CIBIL score (inclusive) for this slab */
	public int getMinCibil() {
		return minCibil;
	}

	/** @param minCibil the minimum CIBIL score to set */
	public void setMinCibil(int minCibil) {
		this.minCibil = minCibil;
	}

	/** @return the maximum CIBIL score (inclusive) for this slab */
	public int getMaxCibil() {
		return maxCibil;
	}

	/** @param maxCibil the maximum CIBIL score to set */
	public void setMaxCibil(int maxCibil) {
		this.maxCibil = maxCibil;
	}

	/** @return the annual interest rate (percentage) for this CIBIL range */
	public double getInterestRate() {
		return interestRate;
	}

	/** @param interestRate the annual interest rate to set */
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	/** @return the bank that owns this interest rate slab */
	public Bank getBank() {
		return bank;
	}

	/** @param bank the owning bank to set */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

    // Getters & Setters
}
