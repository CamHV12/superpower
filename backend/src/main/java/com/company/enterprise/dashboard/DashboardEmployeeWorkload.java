package com.company.enterprise.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record DashboardEmployeeWorkload(
        UUID employeeId,
        String employeeName,
        long openTasks,
        long overdueTasks,
        BigDecimal estimatedHours,
        BigDecimal actualHours
) {}
