package com.api.serviceprovider;

import java.util.List;

import com.api.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.enums.MasterEnums.DocumentLegalServiceProviderStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "legal_providers", indexes = { @Index(name = "idx_provider_name", columnList = "name"),
		@Index(name = "idx_provider_city", columnList = "city") })
public class DocumentLegalServiceProvider extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String city;
	private String state;
	private String country;

	private String address;
	private String phone;
	private String email;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "provider_services", joinColumns = @JoinColumn(name = "provider_id"))
	@Column(name = "service")
	private List<String> services; // e.g. ["TitleSearch","StampDuty","LegalOpinion"]

	private MasterEnums.DocumentLegalServiceProviderStatus status = DocumentLegalServiceProviderStatus.ACTIVE;

	public enum Status {
		ACTIVE, INACTIVE
	}

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public MasterEnums.DocumentLegalServiceProviderStatus getStatus() {
		return status;
	}

	public void setStatus(MasterEnums.DocumentLegalServiceProviderStatus status) {
		this.status = status;
	}

	// Getters and setters omitted for brevity
}
