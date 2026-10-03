package com.company.enterprise.finance.expense;

import com.company.enterprise.finance.expense.dto.CreateExpenseRequest;
import com.company.enterprise.finance.expense.dto.ExpenseResponse;
import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khoản chi")));
    }

    @Transactional
    public void delete(UUID id) {
        Expense expense = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khoản chi"));
        repository.delete(expense);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(), expense.getCategory(), expense.getAmount(),
                expense.getExpenseDate(), expense.getVendor(), expense.getPaymentMethod(),
                expense.getNotes(), expense.getStatus());
    }
}
