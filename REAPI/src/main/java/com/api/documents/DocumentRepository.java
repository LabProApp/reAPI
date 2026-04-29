package com.api.documents;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.enums.MasterEnums;

/**
 * Spring Data JPA repository for {@link Documents} entities.
 *
 * <p>Provides CRUD operations inherited from {@link JpaRepository} as well as
 * custom finder methods used by {@link DocumentsService} to retrieve documents
 * by owner reference ({@code objectType} / {@code objectId}) and by
 * verification status.
 *
 * <p>The {@code IgnoreCase} variants of the finders ensure that callers do not
 * need to normalise the {@code objectType} string before querying.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Documents, Long> {

	/**
	 * Returns all documents whose {@code objectType} matches (case-insensitively)
	 * and whose {@code objectId} equals the supplied value.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @return list of matching {@link Documents}; empty if none found
	 */
	List<Documents> findByObjectTypeIgnoreCaseAndObjectId(String objectType, Long objectId);

	/**
	 * Returns all documents whose {@code objectType} matches (case-insensitively)
	 * and whose {@code objectId} is contained in the supplied list.
	 *
	 * <p>Used for batch loading to avoid N+1 queries when resolving documents
	 * for a collection of parent objects.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectIds  the list of owning entity primary-keys to include
	 * @return list of matching {@link Documents}; empty if none found
	 */
	List<Documents> findByObjectTypeIgnoreCaseAndObjectIdIn(String objectType, List<Long> objectIds);

	/**
	 * Returns all documents whose {@code objectType}, {@code objectId}, and
	 * {@code documentStatus} all match the supplied values.
	 *
	 * @param objectType     the owning entity category (case-insensitive)
	 * @param objectId       the primary-key of the owning entity
	 * @param documentStatus the verification status to filter by
	 * @return list of matching {@link Documents}; empty if none found
	 */
	List<Documents> findByObjectTypeIgnoreCaseAndObjectIdAndDocumentStatus(
			String objectType, Long objectId, MasterEnums.DocumentStatus documentStatus);

	/**
	 * Returns all documents with the given verification status across all
	 * object types.
	 *
	 * <p>Primarily used to retrieve the admin review queue (all
	 * {@link MasterEnums.DocumentStatus#NOT_VERIFIED} documents).
	 *
	 * @param documentStatus the verification status to filter by
	 * @return list of matching {@link Documents}; empty if none found
	 */
	List<Documents> findByDocumentStatus(MasterEnums.DocumentStatus documentStatus);

}
