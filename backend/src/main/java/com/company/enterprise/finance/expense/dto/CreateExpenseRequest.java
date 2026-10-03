package com.company.enterprise.finance.expense.dto;

import com.company.enterprise.finance.payment.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        @NotBlank @Size(max = 100) String category,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull LocalDate expenseDate,
        @Size(max = 200) String vendor,
        @NotNull PaymentMethod paymentMethod,
        @Size(max = 1000) String notes
) {}
