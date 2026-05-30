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
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "client_lead",
	uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "property_id" }),
	indexes = {
		@Index(name = "idx_client_lead_user_id",     columnList = "user_id"),
		@Index(name = "idx_client_lead_property_id", columnList = "property_id"),
		@Index(name = "idx_client_lead_broker_id",   columnList = "broker_id"),
		@Index(name = "idx_client_lead_owner_id",    columnList = "property_owner_id"),
		@Index(name = "idx_client_lead_status",      columnList = "status"),
		@Index(name = "idx_client_lead_mobile",      columnList = "mobile")
	})
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

	
	@PrePersist
	protected void onCreate() {
		inquiryDate = LocalDateTime.now();
	}

	// ─── Getters & Setters ────────────────────────────────────────────────────

	
	public Long getId() { return id; }
	
	public void setId(Long id) { this.id = id; }

	
	public Long getUserId() { return userId; }
	
	public void setUserId(Long userId) { this.userId = userId; }

	
	public Long getPropertyId() { return propertyId; }
	
	public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

	
	public Long getBrokerId() { return brokerId; }
	
	public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

	
	public Long getPropertyOwnerId() { return propertyOwnerId; }
	
	public void setPropertyOwnerId(Long propertyOwnerId) { this.propertyOwnerId = propertyOwnerId; }

	
	public Long getAssignedAgentId() { return assignedAgentId; }
	
	public void setAssignedAgentId(Long assignedAgentId) { this.assignedAgentId = assignedAgentId; }

	
	public String getAssignedAgentName() { return assignedAgentName; }
	
	public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }

	
	public String getClientName() { return clientName; }
	
	public void setClientName(String clientName) { this.clientName = clientName; }

	
	public String getMobile() { return mobile; }
	
	public void setMobile(String mobile) { this.mobile = mobile; }

	
	public String getEmail() { return email; }
	
	public void setEmail(String email) { this.email = email; }

	
	public Integer getAge() { return age; }
	
	public void setAge(Integer age) { this.age = age; }

	
	public Double getMonthlyIncome() { return monthlyIncome; }
	
	public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

	
	public String getProfession() { return profession; }
	
	public void setProfession(String profession) { this.profession = profession; }

	
	public MasterEnums.InquiryType getLeadType() { return leadType; }
	
	public void setLeadType(MasterEnums.InquiryType leadType) { this.leadType = leadType; }

	
	public String getLeadSource() { return leadSource; }
	
	public void setLeadSource(String leadSource) { this.leadSource = leadSource; }

	
	public String getCampaignCode() { return campaignCode; }
	
	public void setCampaignCode(String campaignCode) { this.campaignCode = campaignCode; }

	
	public String getPropertyTitle() { return propertyTitle; }
	
	public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

	
	public String getPropertyCity() { return propertyCity; }
	
	public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }

	
	public String getPropertyState() { return propertyState; }
	
	public void setPropertyState(String propertyState) { this.propertyState = propertyState; }

	
	public String getPropertyLocality() { return propertyLocality; }
	
	public void setPropertyLocality(String propertyLocality) { this.propertyLocality = propertyLocality; }

	
	public String getPropertyType() { return propertyType; }
	
	public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

	
	public Double getPropertyPrice() { return propertyPrice; }
	
	public void setPropertyPrice(Double propertyPrice) { this.propertyPrice = propertyPrice; }

	
	public Boolean getPropertyIdentified() { return propertyIdentified; }
	
	public void setPropertyIdentified(Boolean propertyIdentified) { this.propertyIdentified = propertyIdentified; }

	
	public Double getBudget() { return budget; }
	
	public void setBudget(Double budget) { this.budget = budget; }

	
	public Double getMinBudget() { return minBudget; }
	
	public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

	
	public Double getMaxBudget() { return maxBudget; }
	
	public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

	
	public Double getRequiredLoanAmount() { return requiredLoanAmount; }
	
	public void setRequiredLoanAmount(Double requiredLoanAmount) { this.requiredLoanAmount = requiredLoanAmount; }

	
	public Integer getLoanTenureYears() { return loanTenureYears; }
	
	public void setLoanTenureYears(Integer loanTenureYears) { this.loanTenureYears = loanTenureYears; }

	
	public MasterEnums.LoanType getLoanType() { return loanType; }
	
	public void setLoanType(MasterEnums.LoanType loanType) { this.loanType = loanType; }

	
	public String getPreferredBank() { return preferredBank; }
	
	public void setPreferredBank(String preferredBank) { this.preferredBank = preferredBank; }

	
	public String getDocumentServicesRequired() { return documentServicesRequired; }
	
	public void setDocumentServicesRequired(String documentServicesRequired) { this.documentServicesRequired = documentServicesRequired; }

	
	public String getSpecifications() { return specifications; }
	
	public void setSpecifications(String specifications) { this.specifications = specifications; }

	
	public String getMessage() { return message; }
	
	public void setMessage(String message) { this.message = message; }

	
	public String getRemark() { return remark; }
	
	public void setRemark(String remark) { this.remark = remark; }

	
	public MasterEnums.LeadStatus getStatus() { return status; }
	
	public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }

	
	public LocalDateTime getInquiryDate() { return inquiryDate; }
	
	public void setInquiryDate(LocalDateTime inquiryDate) { this.inquiryDate = inquiryDate; }

	
	public LocalDateTime getContactedDate() { return contactedDate; }
	
	public void setContactedDate(LocalDateTime contactedDate) { this.contactedDate = contactedDate; }

	
	public LocalDateTime getNextFollowUpDate() { return nextFollowUpDate; }
	
	public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }

	
	public LocalDate getExpectedPurchaseDate() { return expectedPurchaseDate; }
	
	public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) { this.expectedPurchaseDate = expectedPurchaseDate; }

	
	public String getBankContactEmail() { return bankContactEmail; }
	
	public void setBankContactEmail(String bankContactEmail) { this.bankContactEmail = bankContactEmail; }

	
	public String getBankContactMobile() { return bankContactMobile; }
	
	public void setBankContactMobile(String bankContactMobile) { this.bankContactMobile = bankContactMobile; }

	
	public String getServiceProviderEmail() { return serviceProviderEmail; }
	
	public void setServiceProviderEmail(String serviceProviderEmail) { this.serviceProviderEmail = serviceProviderEmail; }

	
	public String getServiceProviderMobile() { return serviceProviderMobile; }
	
	public void setServiceProviderMobile(String serviceProviderMobile) { this.serviceProviderMobile = serviceProviderMobile; }

	
	public String getApprovedBank() { return approvedBank; }
	
	public void setApprovedBank(String approvedBank) { this.approvedBank = approvedBank; }

	
	public Double getApprovedLoanAmount() { return approvedLoanAmount; }
	
	public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }

	
	public Double getApprovedInterestRate() { return approvedInterestRate; }
	
	public void setApprovedInterestRate(Double approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }
}
