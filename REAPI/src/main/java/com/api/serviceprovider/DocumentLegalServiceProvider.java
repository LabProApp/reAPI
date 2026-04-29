package com.api.serviceprovider;

import java.util.List;
import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;
import com.api.enums.MasterEnums.DocumentLegalServiceProviderStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a document and legal service provider.
 *
 * <p>Covers professionals such as lawyers, notaries, document registrars, and
 * other legal/documentation service vendors in the real estate ecosystem.
 * Each provider has a legal name, contact details, a list of offered services
 * (e.g., TitleSearch, StampDuty, LegalOpinion), an associated subscription
 * package, and an active/inactive status. Mapped to the
 * {@code document_legal_service_provider} table; services are stored in a
 * separate {@code provider_services} collection table.</p>
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document_legal_service_provider")
@Tag(name = "Document Vendor APIs", description = "Operations related to Document Vendor management")
public class DocumentLegalServiceProvider extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String legalname;
	private String contactname;
	private String city;
	private String state;
	private String country;

	private String address;
	private String phone1;
	private String phone2;
	private String email;

	@Enumerated(EnumType.STRING)
	private MasterEnums.PackageEnum planPackage;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "provider_services", joinColumns = @JoinColumn(name = "provider_id"))
	@Column(name = "service")
	private List<String> services; // e.g. ["TitleSearch","StampDuty","LegalOpinion"]
	@Enumerated(EnumType.STRING)
	private MasterEnums.DocumentLegalServiceProviderStatus status = DocumentLegalServiceProviderStatus.ACTIVE;

	/**
	 * Convenience enum representing the provider's operational status.
	 */
	public enum Status {
		ACTIVE, INACTIVE
	}

	/**
	 * Returns the unique identifier of this provider.
	 *
	 * @return the provider ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this provider.
	 *
	 * @param id the provider ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the registered legal name of the provider.
	 *
	 * @return the legal name
	 */
	public String getLegalname() {
		return legalname;
	}

	/**
	 * Sets the registered legal name of the provider.
	 *
	 * @param legalname the legal name to set
	 */
	public void setLegalname(String legalname) {
		this.legalname = legalname;
	}

	/**
	 * Returns the primary contact person's name at the provider.
	 *
	 * @return the contact name
	 */
	public String getContactname() {
		return contactname;
	}

	/**
	 * Sets the primary contact person's name at the provider.
	 *
	 * @param contactname the contact name to set
	 */
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	/**
	 * Returns the city where the provider operates.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city where the provider operates.
	 *
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the state where the provider operates.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state where the provider operates.
	 *
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the country where the provider operates.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country where the provider operates.
	 *
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Returns the full street address of the provider.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the full street address of the provider.
	 *
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Returns the primary phone number of the provider.
	 *
	 * @return the primary phone number
	 */
	public String getPhone1() {
		return phone1;
	}

	/**
	 * Sets the primary phone number of the provider.
	 *
	 * @param phone the primary phone number to set
	 */
	public void setPhone1(String phone) {
		this.phone1 = phone;
	}

	/**
	 * Returns the secondary phone number of the provider.
	 *
	 * @return the secondary phone number
	 */
	public String getPhone2() {
		return phone2;
	}

	/**
	 * Sets the secondary phone number of the provider.
	 *
	 * @param phone the secondary phone number to set
	 */
	public void setPhone2(String phone) {
		this.phone2 = phone;
	}

	/**
	 * Returns the email address of the provider.
	 *
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of the provider.
	 *
	 * @param email the email address to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the list of services offered by the provider
	 * (e.g., TitleSearch, StampDuty, LegalOpinion).
	 *
	 * @return the list of service identifiers
	 */
	public List<String> getServices() {
		return services;
	}

	/**
	 * Sets the list of services offered by the provider.
	 *
	 * @param services the list of service identifiers to set
	 */
	public void setServices(List<String> services) {
		this.services = services;
	}

	/**
	 * Returns the current operational status of the provider.
	 *
	 * @return the provider status
	 */
	public MasterEnums.DocumentLegalServiceProviderStatus getStatus() {
		return status;
	}

	/**
	 * Sets the current operational status of the provider.
	 *
	 * @param status the provider status to set
	 */
	public void setStatus(MasterEnums.DocumentLegalServiceProviderStatus status) {
		this.status = status;
	}

	/**
	 * Returns the subscription package plan associated with this provider.
	 *
	 * @return the plan package enum value
	 */
	public MasterEnums.PackageEnum getPlanPackage() {
		return planPackage;
	}

	/**
	 * Sets the subscription package plan associated with this provider.
	 *
	 * @param planPackage the plan package enum value to set
	 */
	public void setPlanPackage(MasterEnums.PackageEnum planPackage) {
		this.planPackage = planPackage;
	}

	// Getters and setters omitted for brevity
}
