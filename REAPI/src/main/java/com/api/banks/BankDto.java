package com.api.banks;

import java.util.Date;
import java.util.List;

import com.api.commons.BaseDto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class BankDto extends BaseDto {

	private Long id;

	// Bank / Representative Details
	@NotBlank(message = "Bank name is required")
	private String bankName;
	@NotBlank(message = "Contact name is required")
	private String contactName;
	@NotBlank(message = "Contact number is required")
	private String contactNumber;
	private String email;
	private String branchName;
	private String bankLogoUrl;
	// Address Details
	private String locationAddress;
	private String street;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String websiteUrl;

	// Loan Details
	@PositiveOrZero(message = "Interest rate cannot be negative")
	private double interestRate;
	private String interestType;
	@PositiveOrZero(message = "Processing fee cannot be negative")
	private double processingFee;
	@Min(value = 1, message = "Tenure must be at least 1 year")
	private int tenureYears;
	@PositiveOrZero(message = "Max loan amount cannot be negative")
	private double maxLoanAmount;
	@PositiveOrZero(message = "Min loan amount cannot be negative")
	private double minLoanAmount;
	@Min(value = 300, message = "Min CIBIL score must be at least 300")
	@Max(value = 900, message = "Min CIBIL score cannot exceed 900")
	private int minCibilScore;

	// Eligibility Requirements
	@PositiveOrZero(message = "Minimum income cannot be negative")
	private double minimumIncome;
	private String employmentType;
	@Min(value = 18, message = "Minimum age must be at least 18")
	private int minimumAge;
	@Max(value = 100, message = "Maximum age cannot exceed 100")
	private int maximumAge;

	@AssertTrue(message = "Min loan amount must be less than or equal to max loan amount")
	private boolean isLoanAmountRangeValid() {
		return minLoanAmount <= maxLoanAmount;
	}

	@AssertTrue(message = "Minimum age must be less than maximum age")
	private boolean isAgeRangeValid() {
		return minimumAge == 0 || maximumAge == 0 || minimumAge < maximumAge;
	}
	private String nationalityRequirement;

	// Additional Features
	private boolean prepaymentAllowed;
	private boolean partPaymentAllowed;
	private boolean balanceTransferAvailable;
	private boolean insuranceBundled;
	private String specialOffers;
	private String requiredDocuments;
	private String details;
	private List<InterestRatesDto> interestRates;
	// Metadata
	private Date createdAt;
	private Date updatedAt;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankLogoUrl() {
		return bankLogoUrl;
	}

	public void setBankLogoUrl(String bankLogoUrl) {
		this.bankLogoUrl = bankLogoUrl;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public double getProcessingFee() {
		return processingFee;
	}

	public void setProcessingFee(double processingFee) {
		this.processingFee = processingFee;
	}

	public int getTenureYears() {
		return tenureYears;
	}

	public void setTenureYears(int tenureYears) {
		this.tenureYears = tenureYears;
	}

	public double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public int getMinCibilScore() {
		return minCibilScore;
	}

	public void setMinCibilScore(int minCibilScore) {
		this.minCibilScore = minCibilScore;
	}

	public double getMinimumIncome() {
		return minimumIncome;
	}

	public void setMinimumIncome(double minimumIncome) {
		this.minimumIncome = minimumIncome;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public int getMaximumAge() {
		return maximumAge;
	}

	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	public String getNationalityRequirement() {
		return nationalityRequirement;
	}

	public void setNationalityRequirement(String nationalityRequirement) {
		this.nationalityRequirement = nationalityRequirement;
	}

	public boolean isPrepaymentAllowed() {
		return prepaymentAllowed;
	}

	public void setPrepaymentAllowed(boolean prepaymentAllowed) {
		this.prepaymentAllowed = prepaymentAllowed;
	}

	public boolean isPartPaymentAllowed() {
		return partPaymentAllowed;
	}

	public void setPartPaymentAllowed(boolean partPaymentAllowed) {
		this.partPaymentAllowed = partPaymentAllowed;
	}

	public boolean isBalanceTransferAvailable() {
		return balanceTransferAvailable;
	}

	public void setBalanceTransferAvailable(boolean balanceTransferAvailable) {
		this.balanceTransferAvailable = balanceTransferAvailable;
	}

	public boolean isInsuranceBundled() {
		return insuranceBundled;
	}

	public void setInsuranceBundled(boolean insuranceBundled) {
		this.insuranceBundled = insuranceBundled;
	}

	public String getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(String specialOffers) {
		this.specialOffers = specialOffers;
	}

	public String getRequiredDocuments() {
		return requiredDocuments;
	}

	public void setRequiredDocuments(String requiredDocuments) {
		this.requiredDocuments = requiredDocuments;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<InterestRatesDto> getInterestRates() {
		return interestRates;
	}

	public void setInterestRates(List<InterestRatesDto> interestRates) {
		this.interestRates = interestRates;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
