package com.company.enterprise.dashboard;

public record DashboardOverviewResponse(
        long totalEmployees,
        long activeEmployees,
        long totalProjects,
        long activeProjects,
        long totalCustomers,
        long activeCustomers,
        long totalTasks,
        long overdueTasks
) {}
