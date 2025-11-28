package com.api.tools;

public class AffordabilityRequest {
    private double monthlyIncome;
    private double existingEmi;
    private double interestRate; // annual %
    private int tenureYears;
    private double foirPercent; // default 50%
	public double getMonthlyIncome() {
		return monthlyIncome;
	}
	public void setMonthlyIncome(double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}
	public double getExistingEmi() {
		return existingEmi;
	}
	public void setExistingEmi(double existingEmi) {
		this.existingEmi = existingEmi;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public int getTenureYears() {
		return tenureYears;
	}
	public void setTenureYears(int tenureYears) {
		this.tenureYears = tenureYears;
	}
	public double getFoirPercent() {
		return foirPercent;
	}
	public void setFoirPercent(double foirPercent) {
		this.foirPercent = foirPercent;
	}
}
