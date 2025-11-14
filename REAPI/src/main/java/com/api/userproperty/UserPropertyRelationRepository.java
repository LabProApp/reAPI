package com.api.userproperty;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPropertyRelationRepository extends JpaRepository<UserPropertyRelation, Long> {
	List<UserPropertyRelation> findByUserId(Long userId);

	
	Optional<UserPropertyRelation> findByUserIdAndPropertyId(Long userId, Long propertyId);
	//Optional<UserPropertyRelation> findByUser_IdAndProperty_Id(Long userId, Long propertyId);
}
