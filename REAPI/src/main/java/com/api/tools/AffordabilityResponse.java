package com.api.tools;

public class AffordabilityResponse {
    private double affordableEmi;
    private double eligibleLoanAmount;

    public AffordabilityResponse(double affordableEmi, double eligibleLoanAmount) {
        this.affordableEmi = affordableEmi;
        this.eligibleLoanAmount = eligibleLoanAmount;
    }

	public double getAffordableEmi() {
		return affordableEmi;
	}

	public void setAffordableEmi(double affordableEmi) {
		this.affordableEmi = affordableEmi;
	}

	public double getEligibleLoanAmount() {
		return eligibleLoanAmount;
	}

	public void setEligibleLoanAmount(double eligibleLoanAmount) {
		this.eligibleLoanAmount = eligibleLoanAmount;
	}
}
