package com.api.inquiry;

import java.time.LocalDate;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class InquiryDto extends BaseDto {

	/*
	 * ========================= IDENTIFIERS (SYSTEM) =========================
	 */
	private Long id;

	/*
	 * ========================= APPLICANT DETAILS =========================
	 */
	@NotBlank
	private String applicantName;

	@NotBlank
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
	private String mobileNumber;

	@Email
	private String email;
	private Integer age;
	private Double monthlyIncome;
	@NotNull
	private MasterEnums.InquiryType inquiryType;
	private String comments;

	/*
	 * ========================= LOAN DETAILS =========================
	 */

	private Double budget;
	private Double requiredLoanAmount;
	private Integer loanTenureYears;
	private MasterEnums.LoanType loanType;
	/*
	 * ========================= PROPERTY DETAILS =========================
	 */
	private MasterEnums.PropertyTypeEnum propertyType;

	private String propertyState;
	private String propertyCity;
	private String propertyLocality;

	private Boolean propertyIdentified;

	/*
	 * ========================= LEAD SOURCE =========================
	 */
	private String leadSource; // APP / WEBSITE / WHATSAPP / AGENT
	private String campaignCode; // FB_AD_01, GOOGLE_HOMELOAN

	/*
	 * ========================= BANK & STATUS (OPS) =========================
	 */
	private String preferredBank;

	private MasterEnums.InquiryStatus inquiryStatus;

	private Long assignedAgentId;
	private String assignedAgentName;

	/*
	 * ========================= APPROVAL DETAILS =========================
	 */
	private String approvedBank;
	private Double approvedLoanAmount;
	private Double approvedInterestRate;

	/*
	 * ========================= IMPORTANT DATES =========================
	 */
	private LocalDate expectedPurchaseDate;

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

	public Double getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(Double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public MasterEnums.InquiryType getInquiryType() {
		return inquiryType;
	}

	public void setInquiryType(MasterEnums.InquiryType inquiryType) {
		this.inquiryType = inquiryType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
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

	public MasterEnums.LoanType getLoanType() {
		return loanType;
	}

	public void setLoanType(MasterEnums.LoanType loanType) {
		this.loanType = loanType;
	}

	public MasterEnums.PropertyTypeEnum getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(MasterEnums.PropertyTypeEnum propertyType) {
		this.propertyType = propertyType;
	}

	public String getPropertyState() {
		return propertyState;
	}

	public void setPropertyState(String propertyState) {
		this.propertyState = propertyState;
	}

	public String getPropertyCity() {
		return propertyCity;
	}

	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}

	public String getPropertyLocality() {
		return propertyLocality;
	}

	public void setPropertyLocality(String propertyLocality) {
		this.propertyLocality = propertyLocality;
	}

	public Boolean getPropertyIdentified() {
		return propertyIdentified;
	}

	public void setPropertyIdentified(Boolean propertyIdentified) {
		this.propertyIdentified = propertyIdentified;
	}

	public String getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}

	public String getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(String campaignCode) {
		this.campaignCode = campaignCode;
	}

	public String getPreferredBank() {
		return preferredBank;
	}

	public void setPreferredBank(String preferredBank) {
		this.preferredBank = preferredBank;
	}

	public MasterEnums.InquiryStatus getInquiryStatus() {
		return inquiryStatus;
	}

	public void setInquiryStatus(MasterEnums.InquiryStatus inquiryStatus) {
		this.inquiryStatus = inquiryStatus;
	}

	public Long getAssignedAgentId() {
		return assignedAgentId;
	}

	public void setAssignedAgentId(Long assignedAgentId) {
		this.assignedAgentId = assignedAgentId;
	}

	public String getAssignedAgentName() {
		return assignedAgentName;
	}

	public void setAssignedAgentName(String assignedAgentName) {
		this.assignedAgentName = assignedAgentName;
	}

	public String getApprovedBank() {
		return approvedBank;
	}

	public void setApprovedBank(String approvedBank) {
		this.approvedBank = approvedBank;
	}

	public Double getApprovedLoanAmount() {
		return approvedLoanAmount;
	}

	public void setApprovedLoanAmount(Double approvedLoanAmount) {
		this.approvedLoanAmount = approvedLoanAmount;
	}

	public Double getApprovedInterestRate() {
		return approvedInterestRate;
	}

	public void setApprovedInterestRate(Double approvedInterestRate) {
		this.approvedInterestRate = approvedInterestRate;
	}

	public LocalDate getExpectedPurchaseDate() {
		return expectedPurchaseDate;
	}

	public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) {
		this.expectedPurchaseDate = expectedPurchaseDate;
	}

}
