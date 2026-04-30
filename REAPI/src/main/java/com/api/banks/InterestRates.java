package com.api.banks;

import com.api.commons.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="interest_rates")
public class InterestRates extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private int minCibil;

    
    private int maxCibil;

    
    private double interestRate;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonBackReference
    private Bank bank;

	
	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	
	public int getMinCibil() {
		return minCibil;
	}

	
	public void setMinCibil(int minCibil) {
		this.minCibil = minCibil;
	}

	
	public int getMaxCibil() {
		return maxCibil;
	}

	
	public void setMaxCibil(int maxCibil) {
		this.maxCibil = maxCibil;
	}

	
	public double getInterestRate() {
		return interestRate;
	}

	
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	
	public Bank getBank() {
		return bank;
	}

	
	public void setBank(Bank bank) {
		this.bank = bank;
	}

    // Getters & Setters
}
