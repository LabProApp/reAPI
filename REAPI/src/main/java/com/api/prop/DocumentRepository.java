package com.api.prop;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {
    List<Documents> findByUserId(Long userId);
    List<Documents> findByPropertyId(Long propertyId);
}

