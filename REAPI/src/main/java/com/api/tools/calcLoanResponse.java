package com.api.tools;

import java.math.BigDecimal;
import java.util.List;

public class calcLoanResponse {
	private BigDecimal monthlyPayment;
	private BigDecimal totalPayment;
	private BigDecimal totalInterest;
	private int totalMonths;
	private double affordableEmi;
	public calcLoanResponse(double affordableEmi, double eligibleLoan) {
		super();
		this.affordableEmi = affordableEmi;
		this.eligibleLoan = eligibleLoan;
	}

	public calcLoanResponse() {
		// TODO Auto-generated constructor stub
	}

	private double eligibleLoan;
	private List<AmortizationEntry> amortizationSchedule; // optional

	// getters and setters
	public BigDecimal getMonthlyPayment() {
		return monthlyPayment;
	}

	public void setMonthlyPayment(BigDecimal monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}

	public BigDecimal getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(BigDecimal totalPayment) {
		this.totalPayment = totalPayment;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public int getTotalMonths() {
		return totalMonths;
	}

	public void setTotalMonths(int totalMonths) {
		this.totalMonths = totalMonths;
	}

	public List<AmortizationEntry> getAmortizationSchedule() {
		return amortizationSchedule;
	}

	public void setAmortizationSchedule(List<AmortizationEntry> amortizationSchedule) {
		this.amortizationSchedule = amortizationSchedule;
	}

	public double getAffordableEmi() {
		return affordableEmi;
	}

	public void setAffordableEmi(double affordableEmi) {
		this.affordableEmi = affordableEmi;
	}

	public double getEligibleLoan() {
		return eligibleLoan;
	}

	public void setEligibleLoan(double eligibleLoan) {
		this.eligibleLoan = eligibleLoan;
	}
}
