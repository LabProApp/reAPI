package com.api.loans;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.commons.BaseDto;

public class BankLoanInquiryDto extends BaseDto {

	private Long id;

	private String applicantName;
	private String mobileNumber;
	private String email;
	private Integer age;
	private Integer cibilScore;
	private Double monthlyIncome;

	private String employmentType;
	private String employerName;
	private String designation;

	private Double requiredLoanAmount;
	private Integer loanTenureYears;
	private String loanType;

	private String propertyType;
	private String propertyCity;
	private Double propertyValue;
	private Boolean propertyIdentified;

	private String preferredBank;
	private String inquiryStatus;

	private LocalDate expectedPurchaseDate;

	// Response only
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(Integer cibilScore) {
		this.cibilScore = cibilScore;
	}

	public Double getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(Double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Double getRequiredLoanAmount() {
		return requiredLoanAmount;
	}

	public void setRequiredLoanAmount(Double requiredLoanAmount) {
		this.requiredLoanAmount = requiredLoanAmount;
	}

	public Integer getLoanTenureYears() {
		return loanTenureYears;
	}

	public void setLoanTenureYears(Integer loanTenureYears) {
		this.loanTenureYears = loanTenureYears;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getPropertyCity() {
		return propertyCity;
	}

	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}

	public Double getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(Double propertyValue) {
		this.propertyValue = propertyValue;
	}

	public Boolean getPropertyIdentified() {
		return propertyIdentified;
	}

	public void setPropertyIdentified(Boolean propertyIdentified) {
		this.propertyIdentified = propertyIdentified;
	}

	public String getPreferredBank() {
		return preferredBank;
	}

	public void setPreferredBank(String preferredBank) {
		this.preferredBank = preferredBank;
	}

	public String getInquiryStatus() {
		return inquiryStatus;
	}

	public void setInquiryStatus(String inquiryStatus) {
		this.inquiryStatus = inquiryStatus;
	}

	public LocalDate getExpectedPurchaseDate() {
		return expectedPurchaseDate;
	}

	public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) {
		this.expectedPurchaseDate = expectedPurchaseDate;
	}

	// getters & setters
}
