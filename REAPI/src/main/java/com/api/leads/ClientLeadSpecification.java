package com.api.leads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.api.enums.MasterEnums;

import jakarta.persistence.criteria.Predicate;

/**
 * Factory class that builds JPA {@link Specification} predicates for
 * dynamically filtering {@link ClientLead} entities. All filter parameters are
 * optional; a {@code null} or blank value causes the corresponding predicate
 * to be omitted from the query, allowing any combination of filters to be
 * applied at runtime. Results are always ordered newest-first by
 * {@code inquiryDate}.
 */
public class ClientLeadSpecification {

    /**
     * Builds a composite {@link Specification} for {@link ClientLead} by
     * AND-combining only the non-null filter predicates. The resulting query is
     * ordered descending by {@code inquiryDate}.
     *
     * @param brokerId   filter leads by broker ID; {@code null} to skip
     * @param ownerId    filter leads by property owner ID; {@code null} to skip
     * @param propertyId filter leads by property ID; {@code null} to skip
     * @param userId     filter leads by client user ID; {@code null} to skip
     * @param statuses   filter leads by one or more statuses; {@code null} or empty to skip
     * @param leadType   filter leads by inquiry type; {@code null} to skip
     * @param mobile     partial mobile number match (SQL LIKE); {@code null} or blank to skip
     * @param startDate  include leads with {@code inquiryDate} &gt;= this value;
     *                   {@code null} to skip
     * @param endDate    include leads with {@code inquiryDate} &lt;= this value;
     *                   {@code null} to skip
     * @return a {@link Specification} that applies all supplied filters and orders
     *         results by {@code inquiryDate} descending
     */
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
