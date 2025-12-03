package com.api.banks;

public class InterestRatesDto {

	private Long id;
	private int minCibil;
	private int maxCibil;
	private double interestRate;
	private Long bankId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getMinCibil() {
		return minCibil;
	}

	public void setMinCibil(int minCibil) {
		this.minCibil = minCibil;
	}

	public int getMaxCibil() {
		return maxCibil;
	}

	public void setMaxCibil(int maxCibil) {
		this.maxCibil = maxCibil;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	

	
}
