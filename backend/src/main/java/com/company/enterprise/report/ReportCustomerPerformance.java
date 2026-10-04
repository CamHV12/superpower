package com.company.enterprise.report;

import java.math.BigDecimal;
import java.util.UUID;

public record ReportCustomerPerformance(
        UUID customerId,
        String code,
        String name,
        boolean active,
        long projectCount,
        long activeProjects,
        BigDecimal projectBudget,
        BigDecimal invoicedAmount,
        BigDecimal paidAmount,
        BigDecimal receivableAmount
) {}
