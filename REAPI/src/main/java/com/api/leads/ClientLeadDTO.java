package com.api.leads;

import java.time.LocalDateTime;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

public class ClientLeadDTO extends BaseDto {

	private Long id;

	private Long brokerId;
	private Long userId;
	private Long propertyId;
	private Long propertyOwnerId;

	private String clientName;
	private String mobile;
	private String email;

	private String propertyTitle;
	private String propertyCity;
	private Double propertyPrice;
	private String propertyType;

	// Customer's inquiry message
	private String message;

	private String preferredPropertyType;
	private Double preferredBudget;

	private LocalDateTime inquiryDate;
	private LocalDateTime contactedDate;
	private LocalDateTime nextFollowUpDate;

	private MasterEnums.LeadStatus status;
	private String remark;
	private String leadSource;

	// Whether customer requests WhatsApp notification to broker/owner
	private boolean sendWhatsApp;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Long getBrokerId() { return brokerId; }
	public void setBrokerId(Long brokerId) { this.brokerId = brokerId; }

	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }

	public Long getPropertyId() { return propertyId; }
	public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

	public Long getPropertyOwnerId() { return propertyOwnerId; }
	public void setPropertyOwnerId(Long propertyOwnerId) { this.propertyOwnerId = propertyOwnerId; }

	public String getClientName() { return clientName; }
	public void setClientName(String clientName) { this.clientName = clientName; }

	public String getMobile() { return mobile; }
	public void setMobile(String mobile) { this.mobile = mobile; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPropertyTitle() { return propertyTitle; }
	public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

	public String getPropertyCity() { return propertyCity; }
	public void setPropertyCity(String propertyCity) { this.propertyCity = propertyCity; }

	public Double getPropertyPrice() { return propertyPrice; }
	public void setPropertyPrice(Double propertyPrice) { this.propertyPrice = propertyPrice; }

	public String getPropertyType() { return propertyType; }
	public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

	public String getPreferredPropertyType() { return preferredPropertyType; }
	public void setPreferredPropertyType(String preferredPropertyType) { this.preferredPropertyType = preferredPropertyType; }

	public Double getPreferredBudget() { return preferredBudget; }
	public void setPreferredBudget(Double preferredBudget) { this.preferredBudget = preferredBudget; }

	public LocalDateTime getInquiryDate() { return inquiryDate; }
	public void setInquiryDate(LocalDateTime inquiryDate) { this.inquiryDate = inquiryDate; }

	public LocalDateTime getContactedDate() { return contactedDate; }
	public void setContactedDate(LocalDateTime contactedDate) { this.contactedDate = contactedDate; }

	public LocalDateTime getNextFollowUpDate() { return nextFollowUpDate; }
	public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }

	public MasterEnums.LeadStatus getStatus() { return status; }
	public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }

	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }

	public String getLeadSource() { return leadSource; }
	public void setLeadSource(String leadSource) { this.leadSource = leadSource; }

	public boolean isSendWhatsApp() { return sendWhatsApp; }
	public void setSendWhatsApp(boolean sendWhatsApp) { this.sendWhatsApp = sendWhatsApp; }
}
