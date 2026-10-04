package com.company.enterprise.report;

import java.math.BigDecimal;
import java.util.UUID;

public record ReportProjectPerformance(
        UUID projectId,
        String code,
        String name,
        String customerName,
        String status,
        int progress,
        BigDecimal budget,
        long taskCount,
        long completedTasks,
        long overdueTasks,
        BigDecimal estimatedHours,
        BigDecimal actualHours
) {}
