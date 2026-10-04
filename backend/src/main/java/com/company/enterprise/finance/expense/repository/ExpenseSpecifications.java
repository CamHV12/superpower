package com.company.enterprise.finance.expense.repository;

import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class ExpenseSpecifications {
    private ExpenseSpecifications() {}

    public static Specification<Expense> keywordContains(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("category")), pattern),
                    cb.like(cb.lower(root.get("vendor")), pattern),
                    cb.like(cb.lower(root.get("notes")), pattern)
            );
        };
    }

    public static Specification<Expense> categoryEquals(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Expense> statusEquals(ExpenseStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Expense> paymentMethodEquals(PaymentMethod paymentMethod) {
        return (root, query, cb) -> cb.equal(root.get("paymentMethod"), paymentMethod);
    }

    public static Specification<Expense> expenseDateFrom(LocalDate fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expenseDate"), fromDate);
    }

    public static Specification<Expense> expenseDateTo(LocalDate toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("expenseDate"), toDate);
    }
}
