package com.api.userproperty;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPropertyRelationRepository 
extends JpaRepository<UserPropertyRelation, Long> {

List<UserPropertyRelation> findByUserId(Long userId);

Optional<UserPropertyRelation> findByUserIdAndPropertyId(Long userId, Long propertyId);

List<UserPropertyRelation> findByUserIdAndInquiryTrue(Long userId);

List<UserPropertyRelation> findByUserIdAndFavouriteTrue(Long userId);
}