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

	@OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<InterestRates> interestRates;


	public List<InterestRates> getInterestRates() {
		return interestRates;
	}

	public void setInterestRates(List<InterestRates> interestRates) {
		this.interestRates = interestRates;
	}

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

	public String getBankLogoUrl() {
		return bankLogoUrl;
	}

	public void setBankLogoUrl(String bankLogoUrl) {
		this.bankLogoUrl = bankLogoUrl;
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

	// Timestamp metadata
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", updatable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedAt;

	// Getters and Setters for all fields including new address fields
	// ...

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}
}
