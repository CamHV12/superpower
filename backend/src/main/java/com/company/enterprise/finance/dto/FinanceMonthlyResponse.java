package com.company.enterprise.finance.dto;

import java.math.BigDecimal;

public record FinanceMonthlyResponse(
        String month,
        BigDecimal paidAmount,
        BigDecimal expenseAmount,
        BigDecimal netCashFlow
) {}
