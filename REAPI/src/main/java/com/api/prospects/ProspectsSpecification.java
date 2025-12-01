package com.api.prospects;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ProspectsSpecification {

    public static Specification<Prospects> getSpecification(ProspectsDto searchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDto.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchDto.getName().toLowerCase() + "%"));
            }
            if (searchDto.getEmail() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + searchDto.getEmail().toLowerCase() + "%"));
            }
            if (searchDto.getPhone() != null) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + searchDto.getPhone() + "%"));
            }
            if (searchDto.getInquiryType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("inquiryType"), searchDto.getInquiryType()));
            }
            if (searchDto.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), searchDto.getStatus()));
            }
            if (searchDto.getProfession() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("profession")), "%" + searchDto.getProfession().toLowerCase() + "%"));
            }
            if (searchDto.getPropertyType() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("propertyType")), "%" + searchDto.getPropertyType().toLowerCase() + "%"));
            }
            if (searchDto.getMinBudget() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("budget"), searchDto.getMinBudget()));
            }
            if (searchDto.getMaxBudget() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("budget"), searchDto.getMaxBudget()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
