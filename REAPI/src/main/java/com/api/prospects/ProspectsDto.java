package com.api.prospects;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.api.user.User;

public class ProspectsDto extends BaseDto{

	private Long id;
	private String name;
	private String email;
	private String phone;
	private MasterEnums.InquiryType inquiryType;
	private String profession;
	private Double annualSalary;
	private String message;
	private String address; // New field for user address
	private MasterEnums.InquiryStatus status = MasterEnums.InquiryStatus.NEW; // Default to NEW
	private String documentServicesRequired; // e.g., "Contract Drafting"
	private String propertyType; // e.g., "Apartment, Villa"
	private Double minBudget;
	private Double maxBudget;
	private String specifications; // e.g., "3 BHK, 2 Bathrooms, Garden"
	private User requestedByuser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public MasterEnums.InquiryType getInquiryType() {
		return inquiryType;
	}

	public void setInquiryType(MasterEnums.InquiryType inquiryType) {
		this.inquiryType = inquiryType;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Double getAnnualSalary() {
		return annualSalary;
	}

	public void setAnnualSalary(Double annualSalary) {
		this.annualSalary = annualSalary;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public MasterEnums.InquiryStatus getStatus() {
		return status;
	}

	public void setStatus(MasterEnums.InquiryStatus status) {
		this.status = status;
	}

	public String getDocumentServicesRequired() {
		return documentServicesRequired;
	}

	public void setDocumentServicesRequired(String documentServicesRequired) {
		this.documentServicesRequired = documentServicesRequired;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public Double getMinBudget() {
		return minBudget;
	}

	public void setMinBudget(Double minBudget) {
		this.minBudget = minBudget;
	}

	public Double getMaxBudget() {
		return maxBudget;
	}

	public void setMaxBudget(Double maxBudget) {
		this.maxBudget = maxBudget;
	}

	public String getSpecifications() {
		return specifications;
	}

	public void setSpecifications(String specifications) {
		this.specifications = specifications;
	}

	public User getRequestedByuser() {

		return requestedByuser;
	}

	public void setRequestedByuser(User requestedByuser) {
		this.requestedByuser = requestedByuser;
	}

}
