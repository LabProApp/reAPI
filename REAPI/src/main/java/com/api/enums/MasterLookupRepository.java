package com.api.enums;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterLookupRepository extends JpaRepository<MasterLookup, Long> {

	
	List<MasterLookup> findByTypeIgnoreCaseAndStatusIgnoreCase(String type, String status);

	
	List<MasterLookup> findByParentIdAndStatusAndType(Long parentId, String status, String type);

}
