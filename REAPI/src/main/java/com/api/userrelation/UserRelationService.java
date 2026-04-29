package com.api.userrelation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.user.User;
import com.api.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for user relation operations.
 *
 * <p>Manages directional relationships between two {@link User} records
 * (e.g., CLIENT_OF, AGENT_OF, BROKER_OF). The primary create operation is an
 * upsert: if a relation between the two specified users already exists, its
 * type and comments are updated rather than creating a duplicate. All mutating
 * operations execute within a transaction.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserRelationService {

	@Autowired
	private UserRelationRepository userRelationRepository;
	@Autowired
	private UserRepository userRepository;

	/**
	 * Creates a new user relation, or updates the existing one if a relation
	 * between the same two users already exists (upsert behaviour).
	 *
	 * <p>The {@code userRelation} argument must have its {@code user} and
	 * {@code relatedUser} fields populated with at minimum their IDs (stubs are
	 * acceptable). Both users are verified to exist before the relation is
	 * persisted.</p>
	 *
	 * @param userRelation the relation to create or update
	 * @return the saved or updated {@link UserRelation} entity
	 * @throws RuntimeException if the primary user or related user does not exist
	 */
	public UserRelation createRelation(UserRelation userRelation) {
		Long userId = userRelation.getUser().getId();
		Long relatedUserId = userRelation.getRelatedUser().getId();
		log.info("createRelation - Creating relation [userId={}, relatedUserId={}, type={}]",
				userId, relatedUserId, userRelation.getRelationType());

		User user = userRepository.findById(userId).orElseThrow(() -> {
			log.error("createRelation - User not found id={}", userId);
			return new RuntimeException("User not found: " + userId);
		});
		User relatedUser = userRepository.findById(relatedUserId).orElseThrow(() -> {
			log.error("createRelation - Related user not found id={}", relatedUserId);
			return new RuntimeException("Related user not found: " + relatedUserId);
		});

		Optional<UserRelation> existing = userRelationRepository.findByUserAndRelatedUser(user, relatedUser);
		if (existing.isPresent()) {
			log.info("createRelation - Updating existing relation id={}", existing.get().getId());
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

		UserRelation saved = userRelationRepository.save(relation);
		log.info("createRelation - Relation created with id={}", saved.getId());
		return saved;
	}

	/**
	 * Returns all user relations stored in the system.
	 *
	 * @return a list of all {@link UserRelation} entities
	 */
	public List<UserRelation> getAllRelations() {
		log.info("getAllRelations - Fetching all relations");
		List<UserRelation> relations = userRelationRepository.findAll();
		log.info("getAllRelations - Returned {} relations", relations.size());
		return relations;
	}

	/**
	 * Returns all relations where the specified user is the primary user.
	 *
	 * @param userId the ID of the primary user
	 * @return a list of {@link UserRelation} entities for the given user
	 * @throws RuntimeException if no user exists with the given {@code userId}
	 */
	public List<UserRelation> getRelationsByUser(Long userId) {
		log.info("getRelationsByUser - Fetching relations for userId={}", userId);
		User user = userRepository.findById(userId).orElseThrow(() -> {
			log.error("getRelationsByUser - User not found id={}", userId);
			return new RuntimeException("User not found: " + userId);
		});
		List<UserRelation> relations = userRelationRepository.findByUser(user);
		log.info("getRelationsByUser - Returned {} relations for userId={}", relations.size(), userId);
		return relations;
	}

	/**
	 * Returns all relations where the specified user is the related user.
	 *
	 * @param relatedUserId the ID of the related user
	 * @return a list of {@link UserRelation} entities where the given user is the related party
	 * @throws RuntimeException if no user exists with the given {@code relatedUserId}
	 */
	public List<UserRelation> getRelationsByRelatedUser(Long relatedUserId) {
		log.info("getRelationsByRelatedUser - Fetching relations for relatedUserId={}", relatedUserId);
		User relatedUser = userRepository.findById(relatedUserId).orElseThrow(() -> {
			log.error("getRelationsByRelatedUser - User not found id={}", relatedUserId);
			return new RuntimeException("User not found: " + relatedUserId);
		});
		List<UserRelation> relations = userRelationRepository.findByRelatedUser(relatedUser);
		log.info("getRelationsByRelatedUser - Returned {} relations for relatedUserId={}", relations.size(), relatedUserId);
		return relations;
	}

	/**
	 * Returns all relations for the specified user that match the given relation type.
	 *
	 * @param userId       the ID of the primary user
	 * @param relationType the relation type string to filter by (e.g., "CLIENT_OF")
	 * @return a list of matching {@link UserRelation} entities
	 * @throws RuntimeException if no user exists with the given {@code userId}
	 */
	public List<UserRelation> getUserRelationsByType(Long userId, String relationType) {
		log.info("getUserRelationsByType - Fetching relations for userId={}, type={}", userId, relationType);
		User user = userRepository.findById(userId).orElseThrow(() -> {
			log.error("getUserRelationsByType - User not found id={}", userId);
			return new RuntimeException("User not found: " + userId);
		});
		List<UserRelation> relations = userRelationRepository.findByUserAndRelationType(user, relationType);
		log.info("getUserRelationsByType - Returned {} relations for userId={}, type={}", relations.size(), userId, relationType);
		return relations;
	}

	/**
	 * Deletes the user relation with the given ID.
	 *
	 * <p>No exception is thrown if the relation does not exist.</p>
	 *
	 * @param id the ID of the relation to delete
	 */
	public void deleteRelation(Long id) {
		log.info("deleteRelation - Deleting relation id={}", id);
		userRelationRepository.deleteById(id);
		log.info("deleteRelation - Relation id={} deleted", id);
	}
}
