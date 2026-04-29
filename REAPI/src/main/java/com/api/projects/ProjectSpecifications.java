package com.api.projects;

import org.springframework.data.jpa.domain.Specification;

/**
 * Factory class providing JPA {@link Specification} predicates for filtering {@link Project} entities.
 *
 * <p>Each static method returns a {@link Specification} that either applies an exact-match
 * predicate when the supplied value is non-null, or returns {@code null} (treated by Spring Data
 * as a no-op) when the value is {@code null}. These specifications are designed to be composed
 * with {@link Specification#where(Specification)} and {@link Specification#and(Specification)}
 * to build dynamic queries in {@link ProjectService}.</p>
 */
public class ProjectSpecifications {

    /**
     * Returns a specification that filters projects by exact city match.
     *
     * @param city the city to match, or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code city} is {@code null}
     */
    public static Specification<Project> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(root.get("city"), city);
    }

    /**
     * Returns a specification that filters projects by exact builder name match.
     *
     * @param builderName the builder name to match, or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code builderName} is {@code null}
     */
    public static Specification<Project> hasBuilder(String builderName) {
        return (root, query, cb) ->
                builderName == null ? null : cb.equal(root.get("builderName"), builderName);
    }

    /**
     * Returns a specification that filters projects by exact project status match.
     *
     * @param projectStatus the project status to match (e.g., "Completed"), or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code projectStatus} is {@code null}
     */
    public static Specification<Project> hasStatus(String projectStatus) {
        return (root, query, cb) ->
                projectStatus == null ? null : cb.equal(root.get("projectStatus"), projectStatus);
    }

    /**
     * Returns a specification that filters projects whose minimum price is at least the given value.
     *
     * @param price the lower bound for minimum price, or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code price} is {@code null}
     */
    public static Specification<Project> hasMinPrice(Double price) {
        return (root, query, cb) ->
                price == null ? null : cb.greaterThanOrEqualTo(root.get("minPrice"), price);
    }

    /**
     * Returns a specification that filters projects whose maximum price is at most the given value.
     *
     * @param price the upper bound for maximum price, or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code price} is {@code null}
     */
    public static Specification<Project> hasMaxPrice(Double price) {
        return (root, query, cb) ->
                price == null ? null : cb.lessThanOrEqualTo(root.get("maxPrice"), price);
    }

    /**
     * Returns a specification that filters projects by exact property type match.
     *
     * @param type the property type to match (e.g., "Residential"), or {@code null} to apply no filter
     * @return a {@link Specification} predicate, or {@code null} if {@code type} is {@code null}
     */
    public static Specification<Project> hasPropertyType(String type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("propertyType"), type);
    }
}
