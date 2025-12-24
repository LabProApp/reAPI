package com.api.loans;


import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_inquiry")
public class BankHomeLoanInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Applicant Details
    @Column(nullable = false, length = 100)
    private String applicantName;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer cibilScore;

    @Column(nullable = false)
    private Double monthlyIncome;

    // Employment Details
    @Column(nullable = false, length = 30)
    private String employmentType; // SALARIED / SELF_EMPLOYED

    @Column(length = 100)
    private String employerName;

    @Column(length = 50)
    private String designation;

    // Loan Details
    @Column(nullable = false)
    private Double requiredLoanAmount;

    @Column(nullable = false)
    private Integer loanTenureYears;

    @Column(length = 30)
    private String loanType; // HOME / LAP / BALANCE_TRANSFER

    // Property Details
    @Column(length = 50)
    private String propertyType; // FLAT / VILLA / PLOT

    @Column(length = 100)
    private String propertyCity;

    private Double propertyValue;

    private Boolean propertyIdentified;

    // Bank & Status
    @Column(length = 50)
    private String preferredBank;

    @Column(length = 30)
    private String inquiryStatus; // NEW / IN_PROGRESS / APPROVED / REJECTED

    // Dates
    private LocalDate expectedPurchaseDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Audit
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getCibilScore() {
        return cibilScore;
    }

    public void setCibilScore(Integer cibilScore) {
        this.cibilScore = cibilScore;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getRequiredLoanAmount() {
        return requiredLoanAmount;
    }

    public void setRequiredLoanAmount(Double requiredLoanAmount) {
        this.requiredLoanAmount = requiredLoanAmount;
    }

    public Integer getLoanTenureYears() {
        return loanTenureYears;
    }

    public void setLoanTenureYears(Integer loanTenureYears) {
        this.loanTenureYears = loanTenureYears;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyCity() {
        return propertyCity;
    }

    public void setPropertyCity(String propertyCity) {
        this.propertyCity = propertyCity;
    }

    public Double getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Double propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Boolean getPropertyIdentified() {
        return propertyIdentified;
    }

    public void setPropertyIdentified(Boolean propertyIdentified) {
        this.propertyIdentified = propertyIdentified;
    }

    public String getPreferredBank() {
        return preferredBank;
    }

    public void setPreferredBank(String preferredBank) {
        this.preferredBank = preferredBank;
    }

    public String getInquiryStatus() {
        return inquiryStatus;
    }

    public void setInquiryStatus(String inquiryStatus) {
        this.inquiryStatus = inquiryStatus;
    }

    public LocalDate getExpectedPurchaseDate() {
        return expectedPurchaseDate;
    }

    public void setExpectedPurchaseDate(LocalDate expectedPurchaseDate) {
        this.expectedPurchaseDate = expectedPurchaseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
