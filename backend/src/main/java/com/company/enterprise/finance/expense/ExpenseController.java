package com.company.enterprise.finance.expense;

import com.company.enterprise.finance.expense.dto.CreateExpenseRequest;
import com.company.enterprise.finance.expense.dto.ExpenseResponse;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ExpenseResponse> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        return service.findAll(pageable, keyword, category, status, paymentMethod, fromDate, toDate);
    }

    @GetMapping("/{id}")
    public ExpenseResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PatchMapping("/{id}/cancel")
    public ExpenseResponse cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }
}
