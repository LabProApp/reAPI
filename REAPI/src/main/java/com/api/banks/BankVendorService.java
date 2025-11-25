package com.api.banks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BankVendorService {

	private final BankVendorRepository BankVendorRepository;

	public BankVendorService(BankVendorRepository BankVendorRepository) {
		this.BankVendorRepository = BankVendorRepository;
	}

	public List<BankVendor> getAllvendors() {
		return BankVendorRepository.findAll();
	}

	public BankVendor addvendor(BankVendor vendor) {
		return BankVendorRepository.save(vendor);
	}

	public List<BankVendor> filtervendors(Double maxRate, Integer minCibil) {
		return BankVendorRepository.findAll().stream()
				.filter(vendor -> (maxRate == null || vendor.getInterestRate() <= maxRate))
				.filter(vendor -> (minCibil == null || vendor.getMinCibilScore() <= minCibil)).collect(Collectors.toList());
	}

	public ResponseEntity<?> updatevendor(BankVendor bankvendor) {

	    if (bankvendor.getId() == null) {
	        return ResponseEntity.badRequest().body("vendor ID is required for update");
	    }

	    Optional<BankVendor> optionalvendor = BankVendorRepository.findById(bankvendor.getId());

	    if (optionalvendor.isPresent()) {
	        BankVendor existingvendor = optionalvendor.get();

	        // Update String fields
	        if (bankvendor.getBankName() != null) existingvendor.setBankName(bankvendor.getBankName());
	        if (bankvendor.getContactName() != null) existingvendor.setContactName(bankvendor.getContactName());
	        if (bankvendor.getContactNumber() != null) existingvendor.setContactNumber(bankvendor.getContactNumber());
	        if (bankvendor.getWebsiteUrl() != null) existingvendor.setWebsiteUrl(bankvendor.getWebsiteUrl());
	        if (bankvendor.getLocationAddress() != null) existingvendor.setLocationAddress(bankvendor.getLocationAddress());
	        if (bankvendor.getDetails() != null) existingvendor.setDetails(bankvendor.getDetails());

	        // Update numeric fields
	        if (bankvendor.getInterestRate() > 0) existingvendor.setInterestRate(bankvendor.getInterestRate());
	        if (bankvendor.getTenureYears() > 0) existingvendor.setTenureYears(bankvendor.getTenureYears());
	        if (bankvendor.getMinCibilScore() > 0) existingvendor.setMinCibilScore(bankvendor.getMinCibilScore());

	        BankVendorRepository.save(existingvendor);

	        return ResponseEntity.ok(existingvendor);

	    } else {
	        return ResponseEntity.badRequest().body("Bank vendor not found!");
	    }
	}


}
