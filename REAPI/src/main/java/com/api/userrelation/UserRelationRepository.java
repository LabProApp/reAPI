package com.api.userrelation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.user.User;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {

	
	// 🔹 Get all relations for a given user
	List<UserRelation> findByUser(User user);

	
	// 🔹 Get all relations for a given related user
	List<UserRelation> findByRelatedUser(User relatedUser);

	
	// 🔹 Get relations by relation type (e.g., CLIENT_OF, DEALER_OF)
	List<UserRelation> findByRelationType(String relationType);

	
	// 🔹 Get relations by user and relation type (e.g., all CLIENT_OF under one
	// dealer)
	List<UserRelation> findByUserAndRelationType(User user, String relationType);

	
	// 🔹 Find specific relation between two users
	Optional<UserRelation> findByUserAndRelatedUser(User user, User relatedUser);

	
	// 🔹 Delete all relations for a specific user
	void deleteByUser(User user);

	
	// 🔹 Delete all relations where the user is related to someone
	void deleteByRelatedUser(User relatedUser);
}
