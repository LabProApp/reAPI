package com.api.serviceprovider;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class ProviderSpecification {

    public static Specification<DocumentLegalServiceProvider> hasCity(String city) {
        return (root, query, cb) ->
                (city == null || city.isEmpty())
                        ? null
                        : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<DocumentLegalServiceProvider> hasService(String service) {
        return (root, query, cb) -> {
            if (service == null || service.isEmpty()) return null;

            Join<DocumentLegalServiceProvider, String> services =
                    root.join("services");  // List<String> element-collection join

            return cb.equal(
                    cb.lower(services),  // this refers to the String value
                    service.toLowerCase()
            );
        };
    }

    public static Specification<DocumentLegalServiceProvider> nameContains(String q) {
        return (root, query, cb) ->
                (q == null || q.isEmpty())
                        ? null
                        : cb.like(
                                cb.lower(root.get("name")),
                                "%" + q.toLowerCase() + "%"
                        );
    }

    public static Specification<DocumentLegalServiceProvider> minRating(Double min) {
        return (root, query, cb) ->
                min == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("rating"), min);
    }

    public static Specification<DocumentLegalServiceProvider> statusIs(DocumentLegalServiceProvider.Status status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }
}
