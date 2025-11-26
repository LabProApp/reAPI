package com.api.banks;



import org.springframework.data.jpa.domain.Specification;

public class BankLoanSpecifications {

    public static Specification<BankLoanRepresentative> hasMaxRate(Double maxRate) {
        return (root, query, cb) -> maxRate == null ? null : cb.le(root.get("interestRate"), maxRate);
    }

    public static Specification<BankLoanRepresentative> hasMinCibil(Integer minCibil) {
        return (root, query, cb) -> minCibil == null ? null : cb.ge(root.get("minCibilScore"), minCibil);
    }

    public static Specification<BankLoanRepresentative> hasMaxTenure(Integer maxTenure) {
        return (root, query, cb) -> maxTenure == null ? null : cb.le(root.get("tenureYears"), maxTenure);
    }

    public static Specification<BankLoanRepresentative> hasMinIncome(Double minIncome) {
        return (root, query, cb) -> minIncome == null ? null : cb.ge(root.get("minimumIncome"), minIncome);
    }

    public static Specification<BankLoanRepresentative> hasCity(String city) {
        return (root, query, cb) -> city == null ? null : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<BankLoanRepresentative> hasState(String state) {
        return (root, query, cb) -> state == null ? null : cb.equal(cb.lower(root.get("state")), state.toLowerCase());
    }

    public static Specification<BankLoanRepresentative> hasBank(String bank) {
        return (root, query, cb) -> bank == null ? null : cb.like(cb.lower(root.get("bankName")), "%" + bank.toLowerCase() + "%");
    }

    public static Specification<BankLoanRepresentative> hasPostalCode(String postalCode) {
        return (root, query, cb) -> postalCode == null ? null : cb.equal(cb.lower(root.get("postalCode")), postalCode.toLowerCase());
    }
}
