package com.company.enterprise.dashboard;

import java.util.List;

public record DashboardOperationalResponse(
        List<DashboardStatusCount> projectStatuses,
        List<DashboardStatusCount> taskStatuses,
        List<DashboardEmployeeWorkload> employeeWorkloads,
        List<DashboardCustomerKpi> customerKpis,
        List<DashboardActivity> recentActivities
) {}
