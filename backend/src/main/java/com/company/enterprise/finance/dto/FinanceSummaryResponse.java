package com.company.enterprise.finance.dto;

import java.math.BigDecimal;

public record FinanceSummaryResponse(
        BigDecimal totalInvoiced,
        BigDecimal totalPaid,
        BigDecimal totalReceivable,
        BigDecimal totalExpense,
        BigDecimal netCashFlow,
        long overdueInvoices,
        BigDecimal overdueAmount
) {}
