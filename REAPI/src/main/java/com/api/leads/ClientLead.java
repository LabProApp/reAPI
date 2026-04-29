package com.api.leads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * JPA entity representing a client property inquiry or lead, stored in the
 * {@code client_lead} table. A lead captures the full lifecycle of a client's
 * interest in a property, from initial inquiry through financing, legal
 * services, and final approval. A unique constraint on
 * {@code (user_id, property_id)} prevents duplicate leads for the same
 * client–property combination.
 *
 * <p>Fields are grouped into several logical sections:
 * <ul>
 *   <li><b>Relations</b> — foreign keys to User, Property, Broker, and assigned agent</li>
 *   <li><b>Client Info</b> — name, contact, demographics, and income details</li>
 *   <li><b>Lead Classification</b> — inquiry type, source channel, and campaign</li>
 *   <li><b>Property Details</b> — snapshot from the Property entity at inquiry time</li>
 *   <li><b>Financial Details</b> — budget, loan amount, and preferred bank for loan leads</li>
 *   <li><b>Legal / Document Services</b> — services required for registration or rent agreement leads</li>
 *   <li><b>Communication</b> — client message and internal remarks</li>
 *   <li><b>Status and Dates</b> — lead status, inquiry date, contact dates, and follow-up schedule</li>
 *   <li><b>External Contact Details</b> — routed bank or service provider contact info</li>
 *   <li><b>Approval Details</b> — admin-filled loan approval outcome</li>
 * </ul>
 */
@Entity
@Table(name = "client_lead", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "property_id" }))
public class ClientLead extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ─── Relations ────────────────────────────────────────────────────────────

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "property_id")
	private Long propertyId;

	private Long brokerId;
	private Long propertyOwnerId;
	private Long assignedAgentId;
	private String assignedAgentName;

	// ─── Client Info ──────────────────────────────────────────────────────────

	@Column(nullable = false, length = 100)
	private String clientName;

	@Column(nullable = false, length = 15)
	private String mobile;

	@Column(length = 100)
	private String email;

	private Integer age;
	private Double monthlyIncome;

	@Column(length = 100)
	private String profession;

	// ─── Lead Classification ──────────────────────────────────────────────────

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private MasterEnums.InquiryType leadType;

	@Column(length = 50)
	private String leadSource; // APP, WEBSITE, WHATSAPP, CALL, WALK_IN

	@Column(length = 50)
	private String campaignCode;

	// ─── Property Details ─────────────────────────────────────────────────────
	// (auto-populated from Property entity when propertyId is provided)

	@Column(length = 200)
	private String propertyTitle;

	@Column(length = 100)
	private String propertyCity;

	@Column(length = 100)
	private String propertyState;

	@Column(length = 100)
	private String propertyLocality;

	@Column(length = 50)
	private String propertyType;

	private Double propertyPrice;
	private Boolean propertyIdentified;

	// ─── Financial Details ────────────────────────────────────────────────────
	// (for HOME_LOAN, LAP, BALANCE_TRANSFER, LOAN_TRANSFER leads)

	private Double budget;
	private Double minBudget;
	private Double maxBudget;
	private Double requiredLoanAmount;
	private Integer loanTenureYears;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private MasterEnums.LoanType loanType;

	@Column(length = 100)
	private String preferredBank;

	// ─── Legal / Document Services ────────────────────────────────────────────
	// (for PROPERTY_REGISTRATION, RENT_AGREEMENT, DOCUMENT_SERVICES)

	@Column(length = 200)
	private String documentServicesRequired;

	@Column(length = 500)
	private String specifications;

	// ─── Communication ────────────────────────────────────────────────────────

	@Column(length = 1000)
	private String message;

	@Column(length = 5000)
	private String remark;

	// ─── Status & Dates ───────────────────────────────────────────────────────

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private MasterEnums.LeadStatus status = MasterEnums.LeadStatus.NEW;

	private LocalDateTime inquiryDate;
	private LocalDateTime contactedDate;
	private LocalDateTime nextFollowUpDate;
	private LocalDate expectedPurchaseDate;

	// ─── External Contact Details ─────────────────────────────────────────────
	// (bank or legal service provider, filled when routing the lead)

	@Column(length = 100)
	private String bankContactEmail;

	@Column(length = 15)
	private String bankContactMobile;

	@Column(length = 100)
	private String serviceProviderEmail;

	@Column(length = 15)
	private String serviceProviderMobile;

	// ─── Approval Details (admin fills after loan processing) ─────────────────

	@Column(length = 100)
	private String approvedBank;

	private Double approvedLoanAmount;
	private Double approvedInterestRate;

	/**
	 * JPA lifecycle callback that sets {@code inquiryDate} to the current
	 * timestamp when this lead is first persisted.
	 */
	@PrePersist
	protected void onCreate() {
		inquiryDate = LocalDateTime.now();
	}

	// ─── Getters & Setters ────────────────────────────────────────────────────

	/** @return the surrogate primary key of this lead */
	public Long getId() { return id; }
	/** @param id the surrogate primary key to set */
	public void setId(Long id) { this.id = id; }

	/** @return the ID of the user (client) who submitted the inquiry */
	public Long getUserId() { return userId; }
	/** @param userId the user ID to set */
	public void setUserId(Long userId) { this.userId = userId; }

	/** @return the ID of the property this lead is associated with */
	public Long getPropertyId() { return propertyId; }
	/** @param propertyId the property ID to set */
	public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

	/** @return the ID of the broker linked to this lead */
	public Long getBrokerId() { return brokerId; }
	/** @param brokerId the broker ID to set */
	public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

	/** @return the ID of the property owner */
	public Long getPropertyOwnerId() { return propertyOwnerId; }
	/** @param propertyOwnerId the property owner ID to set */
	public void setPropertyOwnerId(Long propertyOwnerId) { this.propertyOwnerId = propertyOwnerId; }

	/** @return the ID of the agent assigned to handle this lead */
	public Long getAssignedAgentId() { return assignedAgentId; }
	/** @param assignedAgentId the assigned agent ID to set */
	public void setAssignedAgentId(Long assignedAgentId) { this.assignedAgentId = assignedAgentId; }

	/** @return the display name of the assigned agent */
	public String getAssignedAgentName() { return assignedAgentName; }
	/** @param assignedAgentName the assigned agent name to set */
	public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }

	/** @return the full name of the client */
	public String getClientName() { return clientName; }
	/** @param clientName the client name to set */
	public void setClientName(String clientName) { this.clientName = clientName; }

	/** @return the mobile number of the client */
	public String getMobile() { return mobile; }
	/** @param mobile the mobile number to set */
	public void setMobile(String mobile) { this.mobile = mobile; }

	/** @return the email address of the client */
	public String getEmail() { return email; }
	/** @param email the email address to set */
	public void setEmail(String email) { this.email = email; }

	/** @return the age of the client */
	public Integer getAge() { return age; }
	/** @param age the client age to set */
	public void setAge(Integer age) { this.age = age; }

	/** @return the monthly income of the client */
	public Double getMonthlyIncome() { return monthlyIncome; }
	/** @param monthlyIncome the monthly income to set */
	public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

	/** @return the profession of the client */
	public String getProfession() { return profession; }
	/** @param profession the profession to set */
	public void setProfession(String profession) { this.profession = profession; }

	/** @return the type of inquiry (e.g. HOME_LOAN, PROPERTY_REGISTRATION) */
	public MasterEnums.InquiryType getLeadType() { return leadType; }
	/** @param leadType the inquiry type to set */
	public void setLeadType(MasterEnums.InquiryType leadType) { this.leadType = leadType; }

	/** @return the channel through which this lead originated (e.g. APP, WEBSITE, CALL) */
	public String getLeadSource() { return leadSource; }
	/** @param leadSource the lead source channel to set */
	public void setLeadSource(String leadSource) { this.leadSource = leadSource; }

	/** @return the marketing campaign code associated with this lead */
	public String getCampaignCode() { return campaignCode; }
	/** @param campaignCode the campaign code to set */
	public void setCampaignCode(String campaignCode) { this.campaignCode = campaignCode; }

	/** @return the title of the property of interest */
	public String getPropertyTitle() { return propertyTitle; }
	/** @param propertyTitle the property title to set */
	public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

	/** @return the city where the property is located */
	public String getPropertyCity() { return propertyCity; }
	/** @param propertyCity the property city to set */
	public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }

	/** @return the state where the property is located */
	public String getPropertyState() { return propertyState; }
	/** @param propertyState the property state to set */
	public void setPropertyState(String propertyState) { this.propertyState = propertyState; }

	/** @return the locality or neighbourhood of the property */
	public String getPropertyLocality() { return propertyLocality; }
	/** @param propertyLocality the property locality to set */
	public void setPropertyLocality(String propertyLocality) { this.propertyLocality = propertyLocality; }

	/** @return the type of the property (e.g. Apartment, Villa) */
	public String getPropertyType() { return propertyType; }
	/** @param propertyType the property type to set */
	public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

	/** @return the listed price of the property */
	public Double getPropertyPrice() { return propertyPrice; }
	/** @param propertyPrice the property price to set */
	public void setPropertyPrice(Double propertyPrice) { this.propertyPrice = propertyPrice; }

	/** @return {@code true} if the client has already identified a specific property */
	public Boolean getPropertyIdentified() { return propertyIdentified; }
	/** @param propertyIdentified {@code true} if a property has been identified */
	public void setPropertyIdentified(Boolean propertyIdentified) { this.propertyIdentified = propertyIdentified; }

	/** @return the client's overall budget for the purchase */
	public Double getBudget() { return budget; }
	/** @param budget the budget to set */
	public void setBudget(Double budget) { this.budget = budget; }

	/** @return the lower bound of the client's acceptable budget range */
	public Double getMinBudget() { return minBudget; }
	/** @param minBudget the minimum budget to set */
	public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

	/** @return the upper bound of the client's acceptable budget range */
	public Double getMaxBudget() { return maxBudget; }
	/** @param maxBudget the maximum budget to set */
	public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

	/** @return the loan amount the client requires */
	public Double getRequiredLoanAmount() { return requiredLoanAmount; }
	/** @param requiredLoanAmount the required loan amount to set */
	public void setRequiredLoanAmount(Double requiredLoanAmount) { this.requiredLoanAmount = requiredLoanAmount; }

	/** @return the desired loan tenure in years */
	public Integer getLoanTenureYears() { return loanTenureYears; }
	/** @param loanTenureYears the loan tenure to set */
	public void setLoanTenureYears(Integer loanTenureYears) { this.loanTenureYears = loanTenureYears; }

	/** @return the type of loan requested */
	public MasterEnums.LoanType getLoanType() { return loanType; }
	/** @param loanType the loan type to set */
	public void setLoanType(MasterEnums.LoanType loanType) { this.loanType = loanType; }

	/** @return the client's preferred bank for the loan */
	public String getPreferredBank() { return preferredBank; }
	/** @param preferredBank the preferred bank to set */
	public void setPreferredBank(String preferredBank) { this.preferredBank = preferredBank; }

	/** @return a description of the legal or document services required */
	public String getDocumentServicesRequired() { return documentServicesRequired; }
	/** @param documentServicesRequired the document services description to set */
	public void setDocumentServicesRequired(String documentServicesRequired) { this.documentServicesRequired = documentServicesRequired; }

	/** @return additional specifications or requirements from the client */
	public String getSpecifications() { return specifications; }
	/** @param specifications the specifications to set */
	public void setSpecifications(String specifications) { this.specifications = specifications; }

	/** @return the message or query submitted by the client */
	public String getMessage() { return message; }
	/** @param message the client message to set */
	public void setMessage(String message) { this.message = message; }

	/** @return internal remarks added by the handling agent or admin */
	public String getRemark() { return remark; }
	/** @param remark the internal remark to set */
	public void setRemark(String remark) { this.remark = remark; }

	/** @return the current status of the lead in the sales pipeline */
	public MasterEnums.LeadStatus getStatus() { return status; }
	/** @param status the lead status to set */
	public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }

	/** @return the timestamp when the inquiry was first submitted */
	public LocalDateTime getInquiryDate() { return inquiryDate; }
	/** @param inquiryDate the inquiry timestamp to set */
	public void setInquiryDate(LocalDateTime inquiryDate) { this.inquiryDate = inquiryDate; }

	/** @return the timestamp when the client was first contacted */
	public LocalDateTime getContactedDate() { return contactedDate; }
	/** @param contactedDate the first-contact timestamp to set */
	public void setContactedDate(LocalDateTime contactedDate) { this.contactedDate = contactedDate; }

	/** @return the timestamp scheduled for the next follow-up call or meeting */
	public LocalDateTime getNextFollowUpDate() { return nextFollowUpDate; }
	/** @param nextFollowUpDate the next follow-up timestamp to set */
	public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }

	/** @return the date by which the client expects to complete the purchase */
	public LocalDate getExpectedPurchaseDate() { return expectedPurchaseDate; }
	/** @param expectedPurchaseDate the expected purchase date to set */
	public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) { this.expectedPurchaseDate = expectedPurchaseDate; }

	/** @return the email address of the bank contact this lead has been routed to */
	public String getBankContactEmail() { return bankContactEmail; }
	/** @param bankContactEmail the bank contact email to set */
	public void setBankContactEmail(String bankContactEmail) { this.bankContactEmail = bankContactEmail; }

	/** @return the mobile number of the bank contact this lead has been routed to */
	public String getBankContactMobile() { return bankContactMobile; }
	/** @param bankContactMobile the bank contact mobile to set */
	public void setBankContactMobile(String bankContactMobile) { this.bankContactMobile = bankContactMobile; }

	/** @return the email address of the legal service provider for this lead */
	public String getServiceProviderEmail() { return serviceProviderEmail; }
	/** @param serviceProviderEmail the service provider email to set */
	public void setServiceProviderEmail(String serviceProviderEmail) { this.serviceProviderEmail = serviceProviderEmail; }

	/** @return the mobile number of the legal service provider */
	public String getServiceProviderMobile() { return serviceProviderMobile; }
	/** @param serviceProviderMobile the service provider mobile to set */
	public void setServiceProviderMobile(String serviceProviderMobile) { this.serviceProviderMobile = serviceProviderMobile; }

	/** @return the name of the bank that approved the loan */
	public String getApprovedBank() { return approvedBank; }
	/** @param approvedBank the approved bank name to set */
	public void setApprovedBank(String approvedBank) { this.approvedBank = approvedBank; }

	/** @return the loan amount approved by the bank */
	public Double getApprovedLoanAmount() { return approvedLoanAmount; }
	/** @param approvedLoanAmount the approved loan amount to set */
	public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }

	/** @return the annual interest rate at which the loan was approved */
	public Double getApprovedInterestRate() { return approvedInterestRate; }
	/** @param approvedInterestRate the approved interest rate to set */
	public void setApprovedInterestRate(Double approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }
}
