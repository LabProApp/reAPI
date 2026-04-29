package com.api.prop;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Property} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations and
 * defines two custom JPQL queries:
 * <ul>
 *   <li>{@link #search} — a basic filtered search with city, type, category,
 *       area, price, rent/sale, post-date, and poster filters.</li>
 *   <li>{@link #searchAll} — an advanced search that adds title, address,
 *       bedroom/bathroom ranges, a global location keyword (matched against
 *       city, locality, state, address, and title), and an amenity CSV
 *       filter.</li>
 * </ul>
 * All filter parameters accept {@code null} to opt out of that criterion.</p>
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

	/**
	 * Searches for properties matching the supplied basic criteria.
	 *
	 * <p>Any parameter may be {@code null}; a {@code null} value disables that
	 * filter entirely. String comparisons use case-insensitive {@code LIKE}
	 * matching; {@code rentOrSale} uses an exact case-insensitive equality check.</p>
	 *
	 * @param city          city filter (partial match); {@code null} to skip
	 * @param type          property type filter (partial match); {@code null} to skip
	 * @param category      category filter (partial match); {@code null} to skip
	 * @param minArea       minimum super area in sq ft (inclusive); {@code null} to skip
	 * @param maxArea       maximum super area in sq ft (inclusive); {@code null} to skip
	 * @param minPrice      minimum price (inclusive); {@code null} to skip
	 * @param maxPrice      maximum price (inclusive); {@code null} to skip
	 * @param rentOrSale    rent/sale indicator (exact, case-insensitive); {@code null} to skip
	 * @param postDate      earliest post date-time (inclusive); {@code null} to skip
	 * @param postedByUser  ID of the user who posted the listing; {@code null} to skip
	 * @return list of matching {@link Property} entities
	 */
	@Query("""
			    SELECT p FROM Property p
			    WHERE (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
			      AND (:type IS NULL OR LOWER(p.type) LIKE LOWER(CONCAT('%', :type, '%')))
			      AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
			      AND (:minArea IS NULL OR p.superArea >= :minArea)
			      AND (:maxArea IS NULL OR p.superArea <= :maxArea)
			      AND (:minPrice IS NULL OR p.price >= :minPrice)
			      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			      AND (:rentOrSale IS NULL OR LOWER(p.rentOrSale) = LOWER(:rentOrSale))
			      AND (:postDate IS NULL OR p.postDate >= :postDate)
			      AND (:postedByUser IS NULL OR p.postedByUser = :postedByUser)
			""")
	List<Property> search(String city, String type, String category, Double minArea, Double maxArea, Double minPrice,
			Double maxPrice, String rentOrSale, LocalDateTime postDate, Long postedByUser);

	/**
	 * Performs an advanced multi-criteria search across all indexed property fields.
	 *
	 * <p>Any parameter may be {@code null}; a {@code null} value disables that
	 * filter entirely. The {@code location} parameter is matched against
	 * {@code city}, {@code location}, {@code state}, {@code address}, and
	 * {@code title} simultaneously. The {@code amenity} parameter is matched
	 * against the comma-separated amenity string using exact, prefix, suffix,
	 * and infix patterns.</p>
	 *
	 * @param title               title filter (partial match); {@code null} to skip
	 * @param address             address filter (partial match); {@code null} to skip
	 * @param city                city filter (partial match); {@code null} to skip
	 * @param type                property type filter (partial match); {@code null} to skip
	 * @param category            category filter (partial match); {@code null} to skip
	 * @param postedBy            poster type filter (partial match); {@code null} to skip
	 * @param constructionStatus  construction status filter (partial match); {@code null} to skip
	 * @param currency            currency code filter (partial match); {@code null} to skip
	 * @param location            global location keyword matched across multiple fields; {@code null} to skip
	 * @param minPrice            minimum price (inclusive); {@code null} to skip
	 * @param maxPrice            maximum price (inclusive); {@code null} to skip
	 * @param minBedrooms         minimum bedroom count (inclusive); {@code null} to skip
	 * @param maxBedrooms         maximum bedroom count (inclusive); {@code null} to skip
	 * @param minBathrooms        minimum bathroom count (inclusive); {@code null} to skip
	 * @param maxBathrooms        maximum bathroom count (inclusive); {@code null} to skip
	 * @param minArea             minimum super area in sq ft (inclusive); {@code null} to skip
	 * @param maxArea             maximum super area in sq ft (inclusive); {@code null} to skip
	 * @param amenity             single amenity ID to match in the CSV amenities field; {@code null} to skip
	 * @param rentOrSale          rent/sale indicator (exact, case-insensitive); {@code null} to skip
	 * @param postDate            earliest post date-time (inclusive); {@code null} to skip
	 * @param postedByUser        ID of the user who posted the listing; {@code null} to skip
	 * @return distinct list of matching {@link Property} entities
	 */
	@Query("""
			SELECT DISTINCT p FROM Property p
			WHERE
			    (:title IS NULL OR LOWER(p.title) LIKE CONCAT('%', LOWER(:title), '%'))
			AND (:address IS NULL OR LOWER(p.address) LIKE CONCAT('%', LOWER(:address), '%'))
			AND (:city IS NULL OR LOWER(p.city) LIKE CONCAT('%', LOWER(:city), '%'))
			AND (:type IS NULL OR LOWER(p.type) LIKE CONCAT('%', LOWER(:type), '%'))
			AND (:category IS NULL OR LOWER(p.category) LIKE CONCAT('%', LOWER(:category), '%'))
			AND (:postedBy IS NULL OR LOWER(p.postedBy) LIKE CONCAT('%', LOWER(:postedBy), '%'))
			AND (:constructionStatus IS NULL OR LOWER(p.constructionStatus) LIKE CONCAT('%', LOWER(:constructionStatus), '%'))
			AND (:currency IS NULL OR LOWER(p.currency) LIKE CONCAT('%', LOWER(:currency), '%'))

			/* 🔥 GLOBAL SEARCH (MOST IMPORTANT FIX) */
			AND (
			    :location IS NULL OR (
			        LOWER(p.city) LIKE CONCAT('%', LOWER(:location), '%')
			        OR LOWER(p.location) LIKE CONCAT('%', LOWER(:location), '%')
			        OR LOWER(p.state) LIKE CONCAT('%', LOWER(:location), '%')
			        OR LOWER(p.address) LIKE CONCAT('%', LOWER(:location), '%')
			        OR LOWER(p.title) LIKE CONCAT('%', LOWER(:location), '%')
			    )
			)

			AND (:minPrice IS NULL OR p.price >= :minPrice)
			AND (:maxPrice IS NULL OR p.price <= :maxPrice)

			AND (:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms)
			AND (:maxBedrooms IS NULL OR p.bedrooms <= :maxBedrooms)

			AND (:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms)
			AND (:maxBathrooms IS NULL OR p.bathrooms <= :maxBathrooms)

			AND (:minArea IS NULL OR p.superArea >= :minArea)
			AND (:maxArea IS NULL OR p.superArea <= :maxArea)

			AND (:rentOrSale IS NULL OR LOWER(p.rentOrSale) = LOWER(:rentOrSale))

			AND (:postDate IS NULL OR p.postDate >= :postDate)

			AND (:postedByUser IS NULL OR p.postedByUser = :postedByUser)

			/* 🔥 CASE-INSENSITIVE AMENITY MATCH */
			AND (
			    :amenity IS NULL
			    OR LOWER(p.amenities) = LOWER(:amenity)
			    OR LOWER(p.amenities) LIKE CONCAT(LOWER(:amenity), ',%')
			    OR LOWER(p.amenities) LIKE CONCAT('%,', LOWER(:amenity))
			    OR LOWER(p.amenities) LIKE CONCAT('%,', LOWER(:amenity), ',%')
			)
			""")
			List<Property> searchAll(
			    String title,
			    String address,
			    String city,
			    String type,
			    String category,
			    String postedBy,
			    String constructionStatus,
			    String currency,
			    String location,
			    Double minPrice,
			    Double maxPrice,
			    Integer minBedrooms,
			    Integer maxBedrooms,
			    Integer minBathrooms,
			    Integer maxBathrooms,
			    Double minArea,
			    Double maxArea,
			    String amenity,
			    String rentOrSale,
			    LocalDateTime postDate,
			    Long postedByUser
			);

}
