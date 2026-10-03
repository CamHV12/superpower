package com.company.enterprise.finance.invoice.repository;

import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public final class InvoiceSpecifications {
    private InvoiceSpecifications() {}

    public static Specification<Invoice> keywordContains(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("invoiceNumber")), pattern),
                    cb.like(cb.lower(root.get("customer").get("name")), pattern)
            );
        };
    }

    public static Specification<Invoice> statusEquals(InvoiceStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Invoice> customerEquals(UUID customerId) {
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Invoice> issueDateFrom(LocalDate fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("issueDate"), fromDate);
    }

    public static Specification<Invoice> issueDateTo(LocalDate toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("issueDate"), toDate);
    }
}
