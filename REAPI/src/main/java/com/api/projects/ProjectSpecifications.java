package com.api.projects;

import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

    public static Specification<Project> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(root.get("city"), city);
    }

    public static Specification<Project> hasBuilder(String builderName) {
        return (root, query, cb) ->
                builderName == null ? null : cb.equal(root.get("builderName"), builderName);
    }

    public static Specification<Project> hasStatus(String projectStatus) {
        return (root, query, cb) ->
                projectStatus == null ? null : cb.equal(root.get("projectStatus"), projectStatus);
    }

    public static Specification<Project> hasMinPrice(Double price) {
        return (root, query, cb) ->
                price == null ? null : cb.greaterThanOrEqualTo(root.get("minPrice"), price);
    }

    public static Specification<Project> hasMaxPrice(Double price) {
        return (root, query, cb) ->
                price == null ? null : cb.lessThanOrEqualTo(root.get("maxPrice"), price);
    }

    public static Specification<Project> hasPropertyType(String type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("propertyType"), type);
    }
}
