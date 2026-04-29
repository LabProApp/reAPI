package com.api.documents;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.enums.MasterEnums;

@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {

	List<Documents> findByObjectTypeIgnoreCaseAndObjectId(String objectType, Long objectId);

	List<Documents> findByObjectTypeIgnoreCaseAndObjectIdIn(String objectType, List<Long> objectIds);

	List<Documents> findByObjectTypeIgnoreCaseAndObjectIdAndDocumentStatus(
			String objectType, Long objectId, MasterEnums.DocumentStatus documentStatus);

	List<Documents> findByDocumentStatus(MasterEnums.DocumentStatus documentStatus);

}

