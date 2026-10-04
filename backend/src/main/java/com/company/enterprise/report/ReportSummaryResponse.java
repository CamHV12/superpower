package com.company.enterprise.report;

import java.math.BigDecimal;

public record ReportSummaryResponse(
        BigDecimal invoicedAmount,
        BigDecimal paidAmount,
        BigDecimal expenseAmount,
        BigDecimal receivableAmount,
        BigDecimal netCashFlow,
        long invoiceCount,
        long paidInvoiceCount,
        long overdueInvoiceCount,
        long projectCount,
        long activeProjectCount,
        long taskCount,
        long completedTaskCount,
        long customerCount,
        long activeCustomerCount
) {}
