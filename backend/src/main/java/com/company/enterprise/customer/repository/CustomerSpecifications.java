package com.company.enterprise.customer.repository;

import com.company.enterprise.customer.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecifications {
    private CustomerSpecifications() {}

    public static Specification<Customer> keywordContains(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern),
                    cb.like(cb.lower(root.get("taxCode")), pattern),
                    cb.like(cb.lower(root.get("contactPerson")), pattern)
            );
        };
    }

    public static Specification<Customer> activeEquals(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
}
