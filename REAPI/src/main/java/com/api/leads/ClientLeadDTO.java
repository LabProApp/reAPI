package com.api.leads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object mirroring the {@link ClientLead} entity. Used for both
 * inbound create/update requests and outbound API responses. On read operations
 * the {@code ownerName}, {@code ownerMobile}, {@code ownerEmail}, and
 * {@code brokerName} fields are populated from the User repository and are
 * ignored on write. The {@code sendWhatsApp} flag controls whether a WhatsApp
 * notification is dispatched when a lead is created.
 */
public class ClientLeadDTO extends BaseDto {

	private Long id;

	// ─── Relations ────────────────────────────────────────────────────────────

	private Long userId;
	private Long propertyId;
	private Long brokerId;
	private Long propertyOwnerId;
	private Long assignedAgentId;
	private String assignedAgentName;

	// ─── Client Info ──────────────────────────────────────────────────────────

	@NotBlank(message = "clientName is required")
	private String clientName;

	@NotBlank(message = "mobile is required")
	private String mobile;

	private String email;
	private Integer age;
	private Double monthlyIncome;
	private String profession;

	// ─── Lead Classification ──────────────────────────────────────────────────

	@NotNull(message = "leadType is required")
	private MasterEnums.InquiryType leadType;

	private String leadSource;
	private String campaignCode;

	// ─── Property Details ─────────────────────────────────────────────────────

	private String propertyTitle;
	private String propertyCity;
	private String propertyState;
	private String propertyLocality;
	private String propertyType;
	private Double propertyPrice;
	private Boolean propertyIdentified;

	// ─── Financial Details ────────────────────────────────────────────────────

	private Double budget;
	private Double minBudget;
	private Double maxBudget;
	private Double requiredLoanAmount;
	private Integer loanTenureYears;
	private MasterEnums.LoanType loanType;
	private String preferredBank;

	// ─── Legal / Document Services ────────────────────────────────────────────

	private String documentServicesRequired;
	private String specifications;

	// ─── Communication ────────────────────────────────────────────────────────

	private String message;
	private String remark;

	// ─── Status & Dates ───────────────────────────────────────────────────────

	private MasterEnums.LeadStatus status;
	private LocalDateTime inquiryDate;
	private LocalDateTime contactedDate;
	private LocalDateTime nextFollowUpDate;
	private LocalDate expectedPurchaseDate;

	// ─── External Contact Details ─────────────────────────────────────────────

	private String bankContactEmail;
	private String bankContactMobile;
	private String serviceProviderEmail;
	private String serviceProviderMobile;

	// ─── Approval Details ─────────────────────────────────────────────────────

	private String approvedBank;
	private Double approvedLoanAmount;
	private Double approvedInterestRate;

	// ─── Resolved Display Fields (populated on read, ignored on write) ──────────

	private String ownerName;
	private String ownerMobile;
	private String ownerEmail;
	private String brokerName;

	// ─── Notification Preference ──────────────────────────────────────────────

	private boolean sendWhatsApp;

	// ─── Getters & Setters ────────────────────────────────────────────────────

	/** @return the surrogate primary key of the lead */
	public Long getId() { return id; }
	/** @param id the surrogate primary key to set */
	public void setId(Long id) { this.id = id; }

	/** @return the ID of the user (client) who submitted the inquiry */
	public Long getUserId() { return userId; }
	/** @param userId the user ID to set */
	public void setUserId(Long userId) { this.userId = userId; }

	/** @return the ID of the property associated with this lead */
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

	/** @return the ID of the agent assigned to this lead */
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
	/** @param age the age to set */
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

	/** @return the channel through which this lead originated */
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

	/** @return the locality of the property */
	public String getPropertyLocality() { return propertyLocality; }
	/** @param propertyLocality the property locality to set */
	public void setPropertyLocality(String propertyLocality) { this.propertyLocality = propertyLocality; }

	/** @return the type of property (e.g. Apartment, Villa) */
	public String getPropertyType() { return propertyType; }
	/** @param propertyType the property type to set */
	public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

	/** @return the listed price of the property */
	public Double getPropertyPrice() { return propertyPrice; }
	/** @param propertyPrice the property price to set */
	public void setPropertyPrice(Double propertyPrice) { this.propertyPrice = propertyPrice; }

	/** @return {@code true} if the client has identified a specific property */
	public Boolean getPropertyIdentified() { return propertyIdentified; }
	/** @param propertyIdentified {@code true} if a property has been identified */
	public void setPropertyIdentified(Boolean propertyIdentified) { this.propertyIdentified = propertyIdentified; }

	/** @return the client's overall budget for the purchase */
	public Double getBudget() { return budget; }
	/** @param budget the budget to set */
	public void setBudget(Double budget) { this.budget = budget; }

	/** @return the lower bound of the client's budget range */
	public Double getMinBudget() { return minBudget; }
	/** @param minBudget the minimum budget to set */
	public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

	/** @return the upper bound of the client's budget range */
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

	/** @return a description of the document or legal services required */
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

	/** @return the timestamp when the inquiry was submitted */
	public LocalDateTime getInquiryDate() { return inquiryDate; }
	/** @param inquiryDate the inquiry timestamp to set */
	public void setInquiryDate(LocalDateTime inquiryDate) { this.inquiryDate = inquiryDate; }

	/** @return the timestamp when the client was first contacted */
	public LocalDateTime getContactedDate() { return contactedDate; }
	/** @param contactedDate the first-contact timestamp to set */
	public void setContactedDate(LocalDateTime contactedDate) { this.contactedDate = contactedDate; }

	/** @return the timestamp scheduled for the next follow-up */
	public LocalDateTime getNextFollowUpDate() { return nextFollowUpDate; }
	/** @param nextFollowUpDate the next follow-up timestamp to set */
	public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }

	/** @return the date by which the client expects to complete the purchase */
	public LocalDate getExpectedPurchaseDate() { return expectedPurchaseDate; }
	/** @param expectedPurchaseDate the expected purchase date to set */
	public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) { this.expectedPurchaseDate = expectedPurchaseDate; }

	/** @return the email address of the bank contact for this lead */
	public String getBankContactEmail() { return bankContactEmail; }
	/** @param bankContactEmail the bank contact email to set */
	public void setBankContactEmail(String bankContactEmail) { this.bankContactEmail = bankContactEmail; }

	/** @return the mobile number of the bank contact for this lead */
	public String getBankContactMobile() { return bankContactMobile; }
	/** @param bankContactMobile the bank contact mobile to set */
	public void setBankContactMobile(String bankContactMobile) { this.bankContactMobile = bankContactMobile; }

	/** @return the email address of the legal service provider */
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

	/** @return the loan amount that was approved */
	public Double getApprovedLoanAmount() { return approvedLoanAmount; }
	/** @param approvedLoanAmount the approved loan amount to set */
	public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }

	/** @return the annual interest rate at which the loan was approved */
	public Double getApprovedInterestRate() { return approvedInterestRate; }
	/** @param approvedInterestRate the approved interest rate to set */
	public void setApprovedInterestRate(Double approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }

	/**
	 * Returns the resolved display name of the property owner (populated on read
	 * from the User repository; ignored on write).
	 *
	 * @return the owner's full name, or {@code null} if not resolved
	 */
	public String getOwnerName() { return ownerName; }
	/** @param ownerName the resolved owner name to set */
	public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

	/**
	 * Returns the resolved mobile number of the property owner (populated on read
	 * from the User repository; ignored on write).
	 *
	 * @return the owner's mobile number, or {@code null} if not resolved
	 */
	public String getOwnerMobile() { return ownerMobile; }
	/** @param ownerMobile the resolved owner mobile to set */
	public void setOwnerMobile(String ownerMobile) { this.ownerMobile = ownerMobile; }

	/**
	 * Returns the resolved email address of the property owner (populated on read
	 * from the User repository; ignored on write).
	 *
	 * @return the owner's email, or {@code null} if not resolved
	 */
	public String getOwnerEmail() { return ownerEmail; }
	/** @param ownerEmail the resolved owner email to set */
	public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

	/**
	 * Returns the resolved display name of the broker (populated on read from
	 * the User repository; ignored on write).
	 *
	 * @return the broker's full name, or {@code null} if not resolved
	 */
	public String getBrokerName() { return brokerName; }
	/** @param brokerName the resolved broker name to set */
	public void setBrokerName(String brokerName) { this.brokerName = brokerName; }

	/**
	 * Returns whether a WhatsApp notification should be sent when this lead is
	 * created. This field is consumed at creation time and has no effect on
	 * subsequent updates.
	 *
	 * @return {@code true} if a WhatsApp notification is requested
	 */
	public boolean isSendWhatsApp() { return sendWhatsApp; }
	/** @param sendWhatsApp {@code true} to request a WhatsApp notification on create */
	public void setSendWhatsApp(boolean sendWhatsApp) { this.sendWhatsApp = sendWhatsApp; }
}
