package com.api.banks;

import java.util.Date;
import java.util.List;

import com.api.commons.BaseDto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for the {@link Bank} entity. Used for both inbound
 * create/update requests and outbound API responses. Bean Validation
 * constraints enforce required fields and value ranges; cross-field
 * {@code @AssertTrue} validators guard logical consistency of the loan amount
 * and age ranges.
 */
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

	/**
	 * Cross-field validator that ensures {@code minLoanAmount} does not exceed
	 * {@code maxLoanAmount}.
	 *
	 * @return {@code true} when the loan amount range is valid
	 */
	@AssertTrue(message = "Min loan amount must be less than or equal to max loan amount")
	private boolean isLoanAmountRangeValid() {
		return minLoanAmount <= maxLoanAmount;
	}

	/**
	 * Cross-field validator that ensures {@code minimumAge} is strictly less
	 * than {@code maximumAge} when both are non-zero.
	 *
	 * @return {@code true} when the age range is logically consistent
	 */
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

	/** @return the URL of the bank's logo image */
	public String getBankLogoUrl() {
		return bankLogoUrl;
	}

	/** @param bankLogoUrl the logo URL to set */
	public void setBankLogoUrl(String bankLogoUrl) {
		this.bankLogoUrl = bankLogoUrl;
	}

	/** @return the name of the primary contact representative */
	public String getContactName() {
		return contactName;
	}

	/** @param contactName the contact representative name to set */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/** @return the contact phone number of the bank representative */
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

	/** @return the full location address of the bank branch */
	public String getLocationAddress() {
		return locationAddress;
	}

	/** @param locationAddress the location address to set */
	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	/** @return the street portion of the bank's address */
	public String getStreet() {
		return street;
	}

	/** @param street the street to set */
	public void setStreet(String street) {
		this.street = street;
	}

	/** @return the city where the bank branch is located */
	public String getCity() {
		return city;
	}

	/** @param city the city to set */
	public void setCity(String city) {
		this.city = city;
	}

	/** @return the state where the bank branch is located */
	public String getState() {
		return state;
	}

	/** @param state the state to set */
	public void setState(String state) {
		this.state = state;
	}

	/** @return the postal code of the bank branch */
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

	/** @return the annual base interest rate (percentage) */
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

	/** @return the maximum loan amount offered */
	public double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	/** @param maxLoanAmount the maximum loan amount to set */
	public void setMaxLoanAmount(double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	/** @return the minimum loan amount offered */
	public double getMinLoanAmount() {
		return minLoanAmount;
	}

	/** @param minLoanAmount the minimum loan amount to set */
	public void setMinLoanAmount(double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	/** @return the minimum CIBIL score required for eligibility */
	public int getMinCibilScore() {
		return minCibilScore;
	}

	/** @param minCibilScore the minimum CIBIL score to set */
	public void setMinCibilScore(int minCibilScore) {
		this.minCibilScore = minCibilScore;
	}

	/** @return the minimum monthly income required for loan eligibility */
	public double getMinimumIncome() {
		return minimumIncome;
	}

	/** @param minimumIncome the minimum income to set */
	public void setMinimumIncome(double minimumIncome) {
		this.minimumIncome = minimumIncome;
	}

	/** @return the employment type requirement (e.g. Salaried, Self-employed) */
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

	/** @return the nationality requirement for loan applicants */
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

	/** @return description of any special offers or promotions */
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

	/** @param requiredDocuments the required documents description to set */
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

	/**
	 * Returns the CIBIL-tiered interest rate slabs associated with this bank.
	 *
	 * @return list of {@link InterestRatesDto}; may be {@code null} on create requests
	 */
	public List<InterestRatesDto> getInterestRates() {
		return interestRates;
	}

	/**
	 * Sets the CIBIL-tiered interest rate slabs for this bank.
	 *
	 * @param interestRates the interest rate DTOs to associate
	 */
	public void setInterestRates(List<InterestRatesDto> interestRates) {
		this.interestRates = interestRates;
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
