package com.api.banks;

public class InterestRatesDto {

	private Long id;
	private Integer minCibil;
	private Integer maxCibil;
	private Double interestRate;
	private Long bankId;

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
