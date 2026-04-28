package com.api.leads;

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

@Entity
@Table(name = "client_lead", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "property_id" }))
public class ClientLead extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Relations
	private Long brokerId;
	private Long userId;
	private Long propertyId;
	private Long propertyOwnerId; // postedByUser from Property

	// Client Info (auto-populated from User if userId is provided)
	private String clientName;
	private String mobile;
	private String email;

	// Property Snapshot (auto-populated from Property if propertyId is provided)
	private String propertyTitle;
	private String propertyCity;
	private Double propertyPrice;
	private String propertyType;

	// Customer's message/inquiry text
	@Column(length = 1000)
	private String message;

	// Client Preference
	private String preferredPropertyType;
	private Double preferredBudget;

	// Dates
	private LocalDateTime inquiryDate;
	private LocalDateTime contactedDate;
	private LocalDateTime nextFollowUpDate;

	// Status
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private MasterEnums.LeadStatus status = MasterEnums.LeadStatus.NEW;

	// Notes
	@Column(length = 5000)
	private String remark;

	// Lead Source
	private String leadSource; // APP, WEBSITE, WHATSAPP, CALL, WALK_IN

	@PrePersist
	protected void onCreate() {
		inquiryDate = LocalDateTime.now();
	}

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
}
