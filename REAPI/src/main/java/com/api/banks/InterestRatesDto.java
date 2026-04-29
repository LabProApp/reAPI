package com.api.banks;

import com.api.commons.BaseDto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for a single CIBIL-score-range interest rate slab.
 * Used for both inbound create/update requests and outbound responses.
 * Wrapper types ({@link Integer}, {@link Double}) are used for all numeric
 * fields so that {@code @NotNull} validation can distinguish a missing value
 * from zero. The {@code @AssertTrue} method {@link #isCibilRangeValid()}
 * enforces the cross-field constraint that {@code minCibil} must not exceed
 * {@code maxCibil}.
 */
public class InterestRatesDto extends BaseDto {

	/** Surrogate primary key; {@code null} for new slabs. */
	private Long id;

	@NotNull(message = "Min CIBIL is required")
	@Min(value = 300, message = "Min CIBIL must be at least 300")
	@Max(value = 900, message = "Min CIBIL cannot exceed 900")
	private Integer minCibil;

	@NotNull(message = "Max CIBIL is required")
	@Min(value = 300, message = "Max CIBIL must be at least 300")
	@Max(value = 900, message = "Max CIBIL cannot exceed 900")
	private Integer maxCibil;

	@NotNull(message = "Interest rate is required")
	@PositiveOrZero(message = "Interest rate cannot be negative")
	private Double interestRate;

	@NotNull(message = "Bank ID is required")
	private Long bankId;

	/**
	 * Cross-field validator ensuring {@code minCibil} does not exceed
	 * {@code maxCibil}. Returns {@code true} (passes validation) when either
	 * field is {@code null} because the individual {@code @NotNull} constraints
	 * will already report a separate error in that case.
	 *
	 * @return {@code true} if the CIBIL range is logically consistent
	 */
	@AssertTrue(message = "Min CIBIL must be less than or equal to Max CIBIL")
	private boolean isCibilRangeValid() {
		return minCibil == null || maxCibil == null || minCibil <= maxCibil;
	}

	/** @return the surrogate primary key of this rate slab */
	public Long getId() {
		return id;
	}

	/** @param id the surrogate primary key to set */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return the minimum CIBIL score (inclusive) for this slab */
	public Integer getMinCibil() {
		return minCibil;
	}

	/** @param minCibil the minimum CIBIL score to set */
	public void setMinCibil(Integer minCibil) {
		this.minCibil = minCibil;
	}

	/** @return the maximum CIBIL score (inclusive) for this slab */
	public Integer getMaxCibil() {
		return maxCibil;
	}

	/** @param maxCibil the maximum CIBIL score to set */
	public void setMaxCibil(Integer maxCibil) {
		this.maxCibil = maxCibil;
	}

	/** @return the annual interest rate (percentage) for this CIBIL range */
	public Double getInterestRate() {
		return interestRate;
	}

	/** @param interestRate the annual interest rate to set */
	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	/** @return the ID of the bank that owns this interest rate slab */
	public Long getBankId() {
		return bankId;
	}

	/** @param bankId the owning bank ID to set */
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

}
