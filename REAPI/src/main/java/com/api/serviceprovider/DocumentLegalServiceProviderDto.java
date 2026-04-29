package com.api.serviceprovider;

import java.util.List;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;
import com.api.enums.MasterEnums.DocumentLegalServiceProviderStatus;

/**
 * Data Transfer Object for {@link DocumentLegalServiceProvider}.
 *
 * <p>Carries document and legal service provider data across the API boundary,
 * mirroring the fields of the entity while decoupling persistence concerns from
 * the API contract. Extends {@link BaseDto} for common audit/metadata fields.</p>
 */
public class DocumentLegalServiceProviderDto extends BaseDto {
	private Long id;
	private String legalname;
	private String contactname;
	private String city;
	private String state;
	private String country;
	private String address;
	private String phone1;
	private String phone2;
	private String email;

	private MasterEnums.PackageEnum planPackage;

	private List<String> services;

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

	/** @return the city where the provider operates */
	public String getCity() {
		return city;
	}

	/** @param city the city to set */
	public void setCity(String city) {
		this.city = city;
	}

	/** @return the state where the provider operates */
	public String getState() {
		return state;
	}

	/** @param state the state to set */
	public void setState(String state) {
		this.state = state;
	}

	/** @return the country where the provider operates */
	public String getCountry() {
		return country;
	}

	/** @param country the country to set */
	public void setCountry(String country) {
		this.country = country;
	}

	/** @return the full street address of the provider */
	public String getAddress() {
		return address;
	}

	/** @param address the address to set */
	public void setAddress(String address) {
		this.address = address;
	}

	/** @return the primary phone number */
	public String getPhone1() {
		return phone1;
	}

	/** @param phone the primary phone number to set */
	public void setPhone1(String phone) {
		this.phone1 = phone;
	}

	/** @return the secondary phone number */
	public String getPhone2() {
		return phone2;
	}

	/** @param phone the secondary phone number to set */
	public void setPhone2(String phone) {
		this.phone2 = phone;
	}

	/** @return the email address of the provider */
	public String getEmail() {
		return email;
	}

	/** @param email the email address to set */
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
