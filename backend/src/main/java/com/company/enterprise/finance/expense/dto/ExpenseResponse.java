package com.company.enterprise.finance.expense.dto;

import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        String category,
        BigDecimal amount,
        LocalDate expenseDate,
        String vendor,
        PaymentMethod paymentMethod,
        String notes,
        ExpenseStatus status
) {}
