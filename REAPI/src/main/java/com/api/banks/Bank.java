package com.api.banks;

import java.util.Date;
import java.util.List;

import com.api.commons.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * JPA entity representing a bank or lending institution partner stored in the
 * {@code bank_partners} table. Holds all bank identification, contact, address,
 * loan product, and eligibility data. The associated CIBIL-tiered interest
 * rates are maintained in a lazily-loaded {@link InterestRates} collection.
 */
@Entity
@Table(name = "bank_partners")
public class Bank extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Bank / Representative Details
	@NotNull
	@Size(max = 100)
	@Column(name = "bank_name", nullable = false)
	private String bankName;

	@Size(max = 500)
	@Column(name = "bank_logoUrl", nullable = false)
	private String bankLogoUrl;

	@NotNull
	@Size(max = 100)
	@Column(name = "contact_name", nullable = false)
	private String contactName;

	@NotNull
	@Size(max = 15)
	@Column(name = "contact_number", nullable = false)
	private String contactNumber;

	@Size(max = 100)
	@Column(name = "email")
	private String email;

	@Size(max = 100)
	@Column(name = "branch_name")
	private String branchName;

	// 🌍 Address Details
	@Size(max = 255)
	@Column(name = "location_address")
	private String locationAddress;

	@Size(max = 100)
	@Column(name = "street")
	private String street;

	@Size(max = 100)
	@Column(name = "city")
	private String city;

	@Size(max = 100)
	@Column(name = "state")
	private String state;

	@Size(max = 20)
	@Column(name = "postal_code")
	private String postalCode;

	@Size(max = 100)
	@Column(name = "country")
	private String country;

	@Size(max = 255)
	@Column(name = "website_url")
	private String websiteUrl;

	@Column(name = "interest_rate")
	private double interestRate; // Annual %

	@Size(max = 20)
	@Column(name = "interest_type")
	private String interestType; // Fixed / Floating / Hybrid

	@Column(name = "processing_fee")
	private double processingFee;

	@Column(name = "tenure_years")
	private int tenureYears;

	@Column(name = "max_loan_amount")
	private double maxLoanAmount;

	@Column(name = "min_loan_amount")
	private double minLoanAmount;

	@Column(name = "min_cibil_score")
	private int minCibilScore;

	// Eligibility Requirements
	@Column(name = "minimum_income")
	private double minimumIncome;

	@Size(max = 50)
	@Column(name = "employment_type")
	private String employmentType; // Salaried / Self-employed

	@Column(name = "minimum_age")
	private int minimumAge;

	@Column(name = "maximum_age")
	private int maximumAge;

	@Size(max = 50)
	@Column(name = "nationality_requirement")
	private String nationalityRequirement;

	// Additional Features
	@Column(name = "prepayment_allowed")
	private boolean prepaymentAllowed;

	@Column(name = "part_payment_allowed")
	private boolean partPaymentAllowed;

	@Column(name = "balance_transfer_available")
	private boolean balanceTransferAvailable;

	@Column(name = "insurance_bundled")
	private boolean insuranceBundled;

	@Size(max = 255)
	@Column(name = "special_offers")
	private String specialOffers;

	@Size(max = 10000)
	@Column(name = "required_documents")
	private String requiredDocuments;

	@Size(max = 3000)
	@Column(name = "details")
	private String details;

	/**
	 * CIBIL-tiered interest rate slabs associated with this bank. Loaded lazily
	 * to avoid unnecessary joins when only the bank summary is needed.
	 */
	@OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<InterestRates> interestRates;

	/**
	 * Returns the list of CIBIL-tiered interest rate slabs for this bank.
	 *
	 * @return list of {@link InterestRates}; may be empty but never {@code null} after persist
	 */
	public List<InterestRates> getInterestRates() {
		return interestRates;
	}

	/**
	 * Sets the list of CIBIL-tiered interest rate slabs for this bank.
	 *
	 * @param interestRates the interest rate slabs to associate
	 */
	public void setInterestRates(List<InterestRates> interestRates) {
		this.interestRates = interestRates;
	}

	/** @return the surrogate primary key of this bank record */
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

	/** @return the branch name of the bank */
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

	/** @return the country where the bank is registered */
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

	/** @return the annual base interest rate (percentage) offered by this bank */
	public double getInterestRate() {
		return interestRate;
	}

	/** @param interestRate the annual base interest rate to set */
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	/** @return the URL of the bank's logo image */
	public String getBankLogoUrl() {
		return bankLogoUrl;
	}

	/** @param bankLogoUrl the logo URL to set */
	public void setBankLogoUrl(String bankLogoUrl) {
		this.bankLogoUrl = bankLogoUrl;
	}

	/** @return the interest type (Fixed, Floating, or Hybrid) */
	public String getInterestType() {
		return interestType;
	}

	/** @param interestType the interest type to set */
	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	/** @return the loan processing fee charged by this bank */
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

	/** @return the maximum loan amount this bank offers */
	public double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	/** @param maxLoanAmount the maximum loan amount to set */
	public void setMaxLoanAmount(double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	/** @return the minimum loan amount this bank offers */
	public double getMinLoanAmount() {
		return minLoanAmount;
	}

	/** @param minLoanAmount the minimum loan amount to set */
	public void setMinLoanAmount(double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	/** @return the minimum CIBIL score required to qualify for a loan */
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

	/** @return the minimum applicant age required for eligibility */
	public int getMinimumAge() {
		return minimumAge;
	}

	/** @param minimumAge the minimum age to set */
	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	/** @return the maximum applicant age allowed for eligibility */
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

	/** @return {@code true} if the bank allows prepayment of the loan */
	public boolean isPrepaymentAllowed() {
		return prepaymentAllowed;
	}

	/** @param prepaymentAllowed {@code true} to allow prepayment */
	public void setPrepaymentAllowed(boolean prepaymentAllowed) {
		this.prepaymentAllowed = prepaymentAllowed;
	}

	/** @return {@code true} if part-payment of the loan is permitted */
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

	/** @param balanceTransferAvailable {@code true} to indicate balance transfer availability */
	public void setBalanceTransferAvailable(boolean balanceTransferAvailable) {
		this.balanceTransferAvailable = balanceTransferAvailable;
	}

	/** @return {@code true} if loan insurance is bundled with this product */
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

	/** @return the list of documents required by this bank for loan processing */
	public String getRequiredDocuments() {
		return requiredDocuments;
	}

	/** @param requiredDocuments the required documents description to set */
	public void setRequiredDocuments(String requiredDocuments) {
		this.requiredDocuments = requiredDocuments;
	}

	/** @return additional details or notes about this bank's loan product */
	public String getDetails() {
		return details;
	}

	/** @param details the additional details to set */
	public void setDetails(String details) {
		this.details = details;
	}

	/** @return the timestamp when this record was created */
	public Date getCreatedAt() {
		return createdAt;
	}

	/** @param createdAt the creation timestamp to set */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/** @return the timestamp when this record was last updated */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/** @param updatedAt the last-updated timestamp to set */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	// Timestamp metadata
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", updatable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedAt;

	// Getters and Setters for all fields including new address fields
	// ...

	/**
	 * JPA lifecycle callback that initialises both {@code createdAt} and
	 * {@code updatedAt} to the current time when this entity is first persisted.
	 */
	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	/**
	 * JPA lifecycle callback that refreshes {@code updatedAt} to the current
	 * time whenever this entity is updated.
	 */
	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}
}
