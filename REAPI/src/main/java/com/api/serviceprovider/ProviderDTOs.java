package com.api.serviceprovider;

import java.time.Instant;
import java.util.List;

import com.api.enums.MasterEnums;

public class ProviderDTOs {
	public static class ProviderResponse {
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
		public MasterEnums.DocumentLegalServiceProviderStatus status;
		public Instant createdAt;
		public Instant updatedAt;
	}
}
