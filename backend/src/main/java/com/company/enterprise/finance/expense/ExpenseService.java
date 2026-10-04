package com.company.enterprise.finance.expense;

import com.company.enterprise.finance.expense.dto.CreateExpenseRequest;
import com.company.enterprise.finance.expense.dto.ExpenseResponse;
import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.expense.repository.ExpenseSpecifications;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ExpenseService {
    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request) {
        return toResponse(repository.save(new Expense(
                request.category(), request.amount(), request.expenseDate(),
                request.vendor(), request.paymentMethod(), request.notes())));
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> findAll(Pageable pageable) {
        return findAll(pageable, null, null, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> findAll(
            Pageable pageable,
            String keyword,
            String category,
            ExpenseStatus status,
            PaymentMethod paymentMethod,
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ");
        }

        Specification<Expense> specification = Specification.where(null);

        if (hasText(keyword)) {
            specification = specification.and(ExpenseSpecifications.keywordContains(keyword));
        }
        if (hasText(category)) {
            specification = specification.and(ExpenseSpecifications.categoryEquals(category.trim()));
        }
        if (status != null) {
            specification = specification.and(ExpenseSpecifications.statusEquals(status));
        }
        if (paymentMethod != null) {
            specification = specification.and(ExpenseSpecifications.paymentMethodEquals(paymentMethod));
        }
        if (fromDate != null) {
            specification = specification.and(ExpenseSpecifications.expenseDateFrom(fromDate));
        }
        if (toDate != null) {
            specification = specification.and(ExpenseSpecifications.expenseDateTo(toDate));
        }

        return repository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khoản chi")));
    }

    @Transactional
    public ExpenseResponse cancel(UUID id) {
        Expense expense = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khoản chi"));
        expense.cancel();
        return toResponse(repository.save(expense));
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(), expense.getCategory(), expense.getAmount(),
                expense.getExpenseDate(), expense.getVendor(), expense.getPaymentMethod(),
                expense.getNotes(), expense.getStatus());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
