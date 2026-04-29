package com.api.leads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.enums.MasterEnums;

public class ClientLeadSummaryDTO {

    private Long id;

    // ─── Lead Classification ──────────────────────────────────────────────────

    private MasterEnums.InquiryType leadType;
    private MasterEnums.LeadStatus status;
    private String leadSource;

    // ─── Customer Info ────────────────────────────────────────────────────────

    private String clientName;
    private String mobile;
    private String email;
    private String profession;
    private Double monthlyIncome;

    // ─── Owner / Broker Info ──────────────────────────────────────────────────

    private Long propertyOwnerId;
    private String ownerName;
    private String ownerMobile;
    private String ownerEmail;

    private Long brokerId;
    private String brokerName;

    private String assignedAgentName;

    // ─── Property Details ─────────────────────────────────────────────────────

    private Long propertyId;
    private String propertyTitle;
    private String propertyCity;
    private String propertyState;
    private String propertyLocality;
    private String propertyType;
    private Double propertyPrice;

    // ─── Financial ────────────────────────────────────────────────────────────

    private Double budget;
    private Double minBudget;
    private Double maxBudget;
    private Double requiredLoanAmount;
    private Integer loanTenureYears;
    private MasterEnums.LoanType loanType;
    private String preferredBank;

    // ─── Legal ────────────────────────────────────────────────────────────────

    private String documentServicesRequired;
    private String specifications;

    // ─── Communication ────────────────────────────────────────────────────────

    private String message;
    private String remark;

    // ─── Dates ───────────────────────────────────────────────────────────────

    private LocalDateTime inquiryDate;
    private LocalDateTime contactedDate;
    private LocalDateTime nextFollowUpDate;
    private LocalDate expectedPurchaseDate;

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MasterEnums.InquiryType getLeadType() { return leadType; }
    public void setLeadType(MasterEnums.InquiryType leadType) { this.leadType = leadType; }

    public MasterEnums.LeadStatus getStatus() { return status; }
    public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }

    public String getLeadSource() { return leadSource; }
    public void setLeadSource(String leadSource) { this.leadSource = leadSource; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Long getPropertyOwnerId() { return propertyOwnerId; }
    public void setPropertyOwnerId(Long propertyOwnerId) { this.propertyOwnerId = propertyOwnerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerMobile() { return ownerMobile; }
    public void setOwnerMobile(String ownerMobile) { this.ownerMobile = ownerMobile; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public Long getBrokerId() { return brokerId; }
    public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }

    public String getAssignedAgentName() { return assignedAgentName; }
    public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

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

    public LocalDateTime getInquiryDate() { return inquiryDate; }
    public void setInquiryDate(LocalDateTime inquiryDate) { this.inquiryDate = inquiryDate; }

    public LocalDateTime getContactedDate() { return contactedDate; }
    public void setContactedDate(LocalDateTime contactedDate) { this.contactedDate = contactedDate; }

    public LocalDateTime getNextFollowUpDate() { return nextFollowUpDate; }
    public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }

    public LocalDate getExpectedPurchaseDate() { return expectedPurchaseDate; }
    public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) { this.expectedPurchaseDate = expectedPurchaseDate; }
}
