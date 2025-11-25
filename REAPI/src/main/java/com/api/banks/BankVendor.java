package com.api.banks;

import com.api.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_vendors")
public class BankVendor extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bankName;
	private String contactName;
	private double interestRate; // Annual interest %
	private int tenureYears; // Max tenure in years
	private String contactNumber;
	private String websiteUrl;
	private String locationAddress; // Branch / region
	private int minCibilScore; // Minimum credit score required
	private String details; // Extra info or features

	 public BankVendor() {
	    }
	 
	public BankVendor(String bankName, double interestRate, int tenureYears, String contactNumber, String websiteUrl,
			String locationAddress, int minCibilScore, String details) {
		this.bankName = bankName;
		this.interestRate = interestRate;
		this.tenureYears = tenureYears;
		this.contactNumber = contactNumber;
		this.websiteUrl = websiteUrl;
		this.locationAddress = locationAddress;
		this.minCibilScore = minCibilScore;
		this.details = details;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
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

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public int getTenureYears() {
		return tenureYears;
	}

	public void setTenureYears(int tenureYears) {
		this.tenureYears = tenureYears;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

	public int getMinCibilScore() {
		return minCibilScore;
	}

	public void setMinCibilScore(int minCibilScore) {
		this.minCibilScore = minCibilScore;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
