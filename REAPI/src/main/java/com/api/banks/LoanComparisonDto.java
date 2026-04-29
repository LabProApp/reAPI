package com.api.banks;

import java.util.Date;

/**
 * Read-only Data Transfer Object used to compare loan offers from multiple
 * banks. Instances are produced by {@link BankService#listComparison()} and
 * are sorted in ascending order of {@code interestRate} so that callers can
 * immediately present a side-by-side view of available loan products. This DTO
 * intentionally omits the nested {@link InterestRatesDto} collection; only the
 * bank-level base interest rate is included for quick comparison.
 */
public class LoanComparisonDto {

	private Long id;

	// Bank / Representative Details
	private String bankName;
	private String contactName;
	private String contactNumber;
	private String email;
	private String branchName;

	// Address Details
	private String locationAddress;
	private String street;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String websiteUrl;

	// Loan Details
	private double interestRate;
	private String interestType;
	private double processingFee;
	private int tenureYears;
	private double maxLoanAmount;
	private double minLoanAmount;
	private int minCibilScore;

	// Eligibility Requirements
	private double minimumIncome;
	private String employmentType;
	private int minimumAge;
	private int maximumAge;
	private String nationalityRequirement;

	// Additional Features
	private boolean prepaymentAllowed;
	private boolean partPaymentAllowed;
	private boolean balanceTransferAvailable;
	private boolean insuranceBundled;
	private String specialOffers;
	private String requiredDocuments;
	private String details;

	// Metadata
	private Date createdAt;
	private Date updatedAt;

	// Getters and Setters

	/** @return the surrogate primary key of the bank */
	public Long getId() {
		return id;
	}

	/** @param id the surrogate primary key to set */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return the name of the bank */
	public String getBankName() {
		return bankName;
	}

	/** @param bankName the bank name to set */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/** @return the name of the primary contact representative */
	public String getContactName() {
		return contactName;
	}

	/** @param contactName the contact name to set */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/** @return the contact phone number */
	public String getContactNumber() {
		return contactNumber;
	}

	/** @param contactNumber the contact number to set */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/** @return the email address of the bank representative */
	public String getEmail() {
		return email;
	}

	/** @param email the email address to set */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return the branch name */
	public String getBranchName() {
		return branchName;
	}

	/** @param branchName the branch name to set */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/** @return the full location address */
	public String getLocationAddress() {
		return locationAddress;
	}

	/** @param locationAddress the location address to set */
	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	/** @return the street portion of the address */
	public String getStreet() {
		return street;
	}

	/** @param street the street to set */
	public void setStreet(String street) {
		this.street = street;
	}

	/** @return the city where the branch is located */
	public String getCity() {
		return city;
	}

	/** @param city the city to set */
	public void setCity(String city) {
		this.city = city;
	}

	/** @return the state where the branch is located */
	public String getState() {
		return state;
	}

	/** @param state the state to set */
	public void setState(String state) {
		this.state = state;
	}

	/** @return the postal code of the branch */
	public String getPostalCode() {
		return postalCode;
	}

	/** @param postalCode the postal code to set */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/** @return the country of the bank */
	public String getCountry() {
		return country;
	}

	/** @param country the country to set */
	public void setCountry(String country) {
		this.country = country;
	}

	/** @return the website URL of the bank */
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	/** @param websiteUrl the website URL to set */
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	/** @return the annual base interest rate (percentage) used for comparison sorting */
	public double getInterestRate() {
		return interestRate;
	}

	/** @param interestRate the annual base interest rate to set */
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	/** @return the interest type (Fixed, Floating, or Hybrid) */
	public String getInterestType() {
		return interestType;
	}

	/** @param interestType the interest type to set */
	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	/** @return the loan processing fee */
	public double getProcessingFee() {
		return processingFee;
	}

	/** @param processingFee the processing fee to set */
	public void setProcessingFee(double processingFee) {
		this.processingFee = processingFee;
	}

	/** @return the maximum loan tenure in years */
	public int getTenureYears() {
		return tenureYears;
	}

	/** @param tenureYears the tenure in years to set */
	public void setTenureYears(int tenureYears) {
		this.tenureYears = tenureYears;
	}

	/** @return the maximum loan amount available */
	public double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	/** @param maxLoanAmount the maximum loan amount to set */
	public void setMaxLoanAmount(double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	/** @return the minimum loan amount available */
	public double getMinLoanAmount() {
		return minLoanAmount;
	}

	/** @param minLoanAmount the minimum loan amount to set */
	public void setMinLoanAmount(double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	/** @return the minimum CIBIL score required to be eligible */
	public int getMinCibilScore() {
		return minCibilScore;
	}

	/** @param minCibilScore the minimum CIBIL score to set */
	public void setMinCibilScore(int minCibilScore) {
		this.minCibilScore = minCibilScore;
	}

	/** @return the minimum monthly income required */
	public double getMinimumIncome() {
		return minimumIncome;
	}

	/** @param minimumIncome the minimum income to set */
	public void setMinimumIncome(double minimumIncome) {
		this.minimumIncome = minimumIncome;
	}

	/** @return the employment type requirement */
	public String getEmploymentType() {
		return employmentType;
	}

	/** @param employmentType the employment type to set */
	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	/** @return the minimum applicant age required */
	public int getMinimumAge() {
		return minimumAge;
	}

	/** @param minimumAge the minimum age to set */
	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	/** @return the maximum applicant age allowed */
	public int getMaximumAge() {
		return maximumAge;
	}

	/** @param maximumAge the maximum age to set */
	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	/** @return the nationality requirement for applicants */
	public String getNationalityRequirement() {
		return nationalityRequirement;
	}

	/** @param nationalityRequirement the nationality requirement to set */
	public void setNationalityRequirement(String nationalityRequirement) {
		this.nationalityRequirement = nationalityRequirement;
	}

	/** @return {@code true} if prepayment is allowed */
	public boolean isPrepaymentAllowed() {
		return prepaymentAllowed;
	}

	/** @param prepaymentAllowed {@code true} to allow prepayment */
	public void setPrepaymentAllowed(boolean prepaymentAllowed) {
		this.prepaymentAllowed = prepaymentAllowed;
	}

	/** @return {@code true} if part-payment is permitted */
	public boolean isPartPaymentAllowed() {
		return partPaymentAllowed;
	}

	/** @param partPaymentAllowed {@code true} to allow part-payment */
	public void setPartPaymentAllowed(boolean partPaymentAllowed) {
		this.partPaymentAllowed = partPaymentAllowed;
	}

	/** @return {@code true} if balance transfer facility is available */
	public boolean isBalanceTransferAvailable() {
		return balanceTransferAvailable;
	}

	/** @param balanceTransferAvailable {@code true} to indicate availability */
	public void setBalanceTransferAvailable(boolean balanceTransferAvailable) {
		this.balanceTransferAvailable = balanceTransferAvailable;
	}

	/** @return {@code true} if insurance is bundled with the loan */
	public boolean isInsuranceBundled() {
		return insuranceBundled;
	}

	/** @param insuranceBundled {@code true} to indicate bundled insurance */
	public void setInsuranceBundled(boolean insuranceBundled) {
		this.insuranceBundled = insuranceBundled;
	}

	/** @return a description of any special offers or promotions */
	public String getSpecialOffers() {
		return specialOffers;
	}

	/** @param specialOffers the special offers description to set */
	public void setSpecialOffers(String specialOffers) {
		this.specialOffers = specialOffers;
	}

	/** @return the required documents description */
	public String getRequiredDocuments() {
		return requiredDocuments;
	}

	/** @param requiredDocuments the required documents to set */
	public void setRequiredDocuments(String requiredDocuments) {
		this.requiredDocuments = requiredDocuments;
	}

	/** @return additional details about this bank's loan product */
	public String getDetails() {
		return details;
	}

	/** @param details the additional details to set */
	public void setDetails(String details) {
		this.details = details;
	}

	/** @return the timestamp when the bank record was created */
	public Date getCreatedAt() {
		return createdAt;
	}

	/** @param createdAt the creation timestamp to set */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/** @return the timestamp when the bank record was last updated */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/** @param updatedAt the last-updated timestamp to set */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
