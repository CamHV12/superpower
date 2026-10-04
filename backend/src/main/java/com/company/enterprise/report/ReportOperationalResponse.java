package com.company.enterprise.report;

import java.math.BigDecimal;
import java.util.List;

public record ReportOperationalResponse(
        List<StatusCount> projectStatuses,
        List<StatusCount> taskStatuses,
        List<EmployeePerformance> employeePerformance,
        List<CustomerPerformance> customerPerformance
) {
    public record StatusCount(String status, long count) {}
    public record EmployeePerformance(
            java.util.UUID employeeId,
            String employeeName,
            long totalTasks,
            long completedTasks,
            long overdueTasks,
            BigDecimal estimatedHours,
            BigDecimal actualHours
    ) {}
    public record CustomerPerformance(
            java.util.UUID customerId,
            String customerName,
            long projects,
            long activeProjects,
            BigDecimal budget
    ) {}
}
