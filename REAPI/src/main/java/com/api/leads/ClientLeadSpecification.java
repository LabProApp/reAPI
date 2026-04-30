package com.api.leads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.api.enums.MasterEnums;

import jakarta.persistence.criteria.Predicate;

public class ClientLeadSpecification {

    
    public static Specification<ClientLead> build(
            Long brokerId, Long ownerId, Long propertyId, Long userId,
            List<MasterEnums.LeadStatus> statuses, MasterEnums.InquiryType leadType,
            String mobile, LocalDateTime startDate, LocalDateTime endDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (brokerId != null)
                predicates.add(cb.equal(root.get("brokerId"), brokerId));
            if (ownerId != null)
                predicates.add(cb.equal(root.get("propertyOwnerId"), ownerId));
            if (propertyId != null)
                predicates.add(cb.equal(root.get("propertyId"), propertyId));
            if (userId != null)
                predicates.add(cb.equal(root.get("userId"), userId));
            if (statuses != null && !statuses.isEmpty())
                predicates.add(root.get("status").in(statuses));
            if (leadType != null)
                predicates.add(cb.equal(root.get("leadType"), leadType));
            if (mobile != null && !mobile.isBlank())
                predicates.add(cb.like(root.get("mobile"), "%" + mobile + "%"));
            if (startDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("inquiryDate"), startDate));
            if (endDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("inquiryDate"), endDate));

            query.orderBy(cb.desc(root.get("inquiryDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
