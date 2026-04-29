package com.api.enums;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link MasterLookup} entities.
 *
 * <p>Provides standard CRUD operations via {@link JpaRepository} as well as
 * custom queries for fetching flat and hierarchical reference data by type,
 * status, and parent identifier.
 */
public interface MasterLookupRepository extends JpaRepository<MasterLookup, Long> {

	/**
	 * Returns all active lookup entries of the specified type, performing a
	 * case-insensitive match on both {@code type} and {@code status}.
	 *
	 * @param type   the category type to filter on (e.g., {@code "CITY"})
	 * @param status the status to filter on (e.g., {@code "ACTIVE"})
	 * @return a list of matching {@link MasterLookup} entries; never {@code null}
	 */
	List<MasterLookup> findByTypeIgnoreCaseAndStatusIgnoreCase(String type, String status);

	/**
	 * Returns child lookup entries that belong to the given parent, match the
	 * specified status, and are of the specified type.
	 *
	 * <p>Typical use case: fetch all {@code CITY} entries under a given {@code STATE}
	 * parent where the cities are {@code ACTIVE}.
	 *
	 * @param parentId the surrogate ID of the parent {@link MasterLookup} entry
	 * @param status   the status to filter on (e.g., {@code "ACTIVE"})
	 * @param type     the category type to filter on (e.g., {@code "CITY"})
	 * @return a list of matching child {@link MasterLookup} entries; never {@code null}
	 */
	List<MasterLookup> findByParentIdAndStatusAndType(Long parentId, String status, String type);

}
