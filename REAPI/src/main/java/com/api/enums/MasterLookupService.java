package com.api.enums;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MasterLookupService {

	private final MasterLookupRepository repository;

	public MasterLookupService(MasterLookupRepository repository) {
		this.repository = repository;
	}

	// Generic master lookup
	public List<MasterLookup> getMasterValues(String type, String status) {
		return repository.findByTypeIgnoreCaseAndStatusIgnoreCase(type, status);
	}

	// Get child lookups (e.g., cities by state)
	public List<MasterLookup> getChildByMaster(Long parentId, String status) {
		return repository.findByParentIdAndStatusIgnoreCase(parentId, status);
	}

}
