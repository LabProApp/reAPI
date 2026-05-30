package com.api.documents;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.enums.MasterEnums;

@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {

	
	List<Documents> findByObjectTypeAndObjectId(String objectType, Long objectId);

	
	List<Documents> findByObjectTypeAndObjectIdIn(String objectType, List<Long> objectIds);

	
	List<Documents> findByObjectTypeAndObjectIdAndDocumentStatus(
			String objectType, Long objectId, MasterEnums.DocumentStatus documentStatus);

	
	List<Documents> findByDocumentStatus(MasterEnums.DocumentStatus documentStatus);

}
