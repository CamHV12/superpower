package com.company.enterprise.report;

import java.util.List;

public record ReportPerformanceResponse(
        List<ReportProjectPerformance> projects,
        List<ReportCustomerPerformance> customers
) {}
