package com.api.userrelation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.user.User;

/**
 * Spring Data JPA repository for {@link UserRelation} entities.
 *
 * <p>Provides derived-query methods for retrieving relations by primary user,
 * related user, relation type, or a combination thereof, as well as bulk-delete
 * helpers for removing all relations associated with a specific user in either
 * role. Extends {@link JpaRepository} for standard CRUD and pagination support.</p>
 */
@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {

	/**
	 * Returns all relations where the given user is the primary user.
	 *
	 * @param user the primary {@link User}
	 * @return list of matching {@link UserRelation} entities
	 */
	// 🔹 Get all relations for a given user
	List<UserRelation> findByUser(User user);

	/**
	 * Returns all relations where the given user is the related user.
	 *
	 * @param relatedUser the related {@link User}
	 * @return list of matching {@link UserRelation} entities
	 */
	// 🔹 Get all relations for a given related user
	List<UserRelation> findByRelatedUser(User relatedUser);

	/**
	 * Returns all relations with the given relation type string.
	 *
	 * @param relationType the relation type (e.g., "CLIENT_OF", "DEALER_OF")
	 * @return list of matching {@link UserRelation} entities
	 */
	// 🔹 Get relations by relation type (e.g., CLIENT_OF, DEALER_OF)
	List<UserRelation> findByRelationType(String relationType);

	/**
	 * Returns all relations for a given primary user that match the specified relation type.
	 *
	 * @param user         the primary {@link User}
	 * @param relationType the relation type string to filter by
	 * @return list of matching {@link UserRelation} entities
	 */
	// 🔹 Get relations by user and relation type (e.g., all CLIENT_OF under one
	// dealer)
	List<UserRelation> findByUserAndRelationType(User user, String relationType);

	/**
	 * Finds a specific relation between two users, if one exists.
	 *
	 * @param user        the primary {@link User}
	 * @param relatedUser the related {@link User}
	 * @return an {@link Optional} containing the relation if found, or empty
	 */
	// 🔹 Find specific relation between two users
	Optional<UserRelation> findByUserAndRelatedUser(User user, User relatedUser);

	/**
	 * Deletes all relations where the given user is the primary user.
	 *
	 * @param user the primary {@link User} whose relations should be removed
	 */
	// 🔹 Delete all relations for a specific user
	void deleteByUser(User user);

	/**
	 * Deletes all relations where the given user is the related user.
	 *
	 * @param relatedUser the related {@link User} whose relations should be removed
	 */
	// 🔹 Delete all relations where the user is related to someone
	void deleteByRelatedUser(User relatedUser);
}
