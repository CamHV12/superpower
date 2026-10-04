package com.company.enterprise.report;

import java.math.BigDecimal;

public record ReportMonthlyPoint(
        String month,
        BigDecimal invoicedAmount,
        BigDecimal paidAmount,
        BigDecimal expenseAmount,
        BigDecimal netCashFlow
) {}
