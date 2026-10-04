package com.company.enterprise.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record DashboardCustomerKpi(
        UUID customerId,
        String customerName,
        long projectCount,
        long activeProjects,
        BigDecimal projectBudget
) {}
