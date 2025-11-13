package com.api.userrelation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import com.api.user.User;
import com.api.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRelationService {

	@Autowired
	private UserRelationRepository userRelationRepository;
	@Autowired
	private UserRepository userRepository; // assuming you have a UserRepository

	/**
	 * Create or update a relation between two users
	 */
	public UserRelation createRelation(UserRelation userRelation) {

		User user = userRepository.findById(userRelation.getUser().getId())
				.orElseThrow(() -> new RuntimeException("User not found: " + userRelation.getUser().getId()));
		User relatedUser = userRepository.findById(userRelation.getRelatedUser().getId()).orElseThrow(
				() -> new RuntimeException("Related user not found: " + userRelation.getRelatedUser().getId()));

		Optional<UserRelation> existing = userRelationRepository.findByUserAndRelatedUser(user, relatedUser);
		if (existing.isPresent()) {
			UserRelation relation = existing.get();
			relation.setRelationType(userRelation.getRelationType());
			relation.setComments(userRelation.getComments());
			return userRelationRepository.save(relation);
		}

		UserRelation relation = new UserRelation();
		relation.setUser(user);
		relation.setRelatedUser(relatedUser);
		relation.setRelationType(userRelation.getRelationType());
		relation.setComments(userRelation.getComments());

		return userRelationRepository.save(relation);
	}

	/**
	 * Get all relations
	 */
	public List<UserRelation> getAllRelations() {
		return userRelationRepository.findAll();
	}

	/**
	 * Get all relations by user
	 */
	public List<UserRelation> getRelationsByUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found: " + userId));
		return userRelationRepository.findByUser(user);
	}

	/**
	 * Get all relations where the given user is related to someone
	 */
	public List<UserRelation> getRelationsByRelatedUser(Long relatedUserId) {
		User relatedUser = userRepository.findById(relatedUserId)
				.orElseThrow(() -> new RuntimeException("User not found: " + relatedUserId));
		return userRelationRepository.findByRelatedUser(relatedUser);
	}

	/**
	 * Get relations by type
	 */
	public List<UserRelation> getUserRelationsByType(Long userId, String relationType) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found: " + userId));
		return userRelationRepository.findByUserAndRelationType(user, relationType);
	}

	/**
	 * Delete relation by ID
	 */
	public void deleteRelation(Long id) {
		userRelationRepository.deleteById(id);
	}
}
