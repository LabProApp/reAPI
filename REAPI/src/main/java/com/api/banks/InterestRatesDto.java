package com.api.banks;

import com.api.commons.BaseDto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class InterestRatesDto extends BaseDto {

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

	@AssertTrue(message = "Min CIBIL must be less than or equal to Max CIBIL")
	private boolean isCibilRangeValid() {
		return minCibil == null || maxCibil == null || minCibil <= maxCibil;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMinCibil() {
		return minCibil;
	}

	public void setMinCibil(Integer minCibil) {
		this.minCibil = minCibil;
	}

	public Integer getMaxCibil() {
		return maxCibil;
	}

	public void setMaxCibil(Integer maxCibil) {
		this.maxCibil = maxCibil;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

}
