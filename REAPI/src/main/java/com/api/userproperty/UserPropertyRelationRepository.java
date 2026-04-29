package com.api.userproperty;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link UserPropertyRelation} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations and
 * exposes derived query methods for the most common lookup patterns:
 * <ul>
 *   <li>All relations for a given user.</li>
 *   <li>The unique relation for a specific user–property pair.</li>
 *   <li>All inquiry interactions for a user.</li>
 *   <li>All favourite interactions for a user.</li>
 * </ul>
 * </p>
 */
public interface UserPropertyRelationRepository
extends JpaRepository<UserPropertyRelation, Long> {

	/**
	 * Returns all user–property relations belonging to the specified user.
	 *
	 * @param userId the ID of the user
	 * @return list of {@link UserPropertyRelation} records for the given user;
	 *         empty list if none exist
	 */
	List<UserPropertyRelation> findByUserId(Long userId);

	/**
	 * Returns the unique relation record for the given user and property combination.
	 *
	 * @param userId     the ID of the user
	 * @param propertyId the ID of the property
	 * @return an {@link Optional} containing the relation if found, or
	 *         {@link Optional#empty()} if no record exists
	 */
	Optional<UserPropertyRelation> findByUserIdAndPropertyId(Long userId, Long propertyId);

	/**
	 * Returns all properties for which the specified user has raised an inquiry.
	 *
	 * @param userId the ID of the user
	 * @return list of {@link UserPropertyRelation} records where
	 *         {@code inquiry == true}; empty list if none exist
	 */
	List<UserPropertyRelation> findByUserIdAndInquiryTrue(Long userId);

	/**
	 * Returns all properties that the specified user has marked as a favourite.
	 *
	 * @param userId the ID of the user
	 * @return list of {@link UserPropertyRelation} records where
	 *         {@code favourite == true}; empty list if none exist
	 */
	List<UserPropertyRelation> findByUserIdAndFavouriteTrue(Long userId);
}
