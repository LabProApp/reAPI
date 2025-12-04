package com.api.serviceprovider;

import java.time.Instant;
import java.util.List;

import com.api.commons.BaseDto;
import com.api.enums.MasterEnums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class DocumentLegalServiceProviderDto extends BaseDto {

	public Long id;
	public String name;
	public String city;
	public String state;
	public String country;
	public String address;
	public String phone;
	public String email;
	public List<String> services;
	public Double rating;
	@Enumerated(EnumType.STRING)
	public MasterEnums.DocumentLegalServiceProviderStatus status;
	public Instant createdAt;
	public Instant updatedAt;
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
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public MasterEnums.DocumentLegalServiceProviderStatus getStatus() {
		return status;
	}
	public void setStatus(MasterEnums.DocumentLegalServiceProviderStatus status) {
		this.status = status;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

}
