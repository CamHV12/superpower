package com.company.enterprise.report;

import java.time.LocalDate;
import java.util.List;

public record ReportResponse(
        LocalDate fromDate,
        LocalDate toDate,
        ReportSummaryResponse summary,
        List<ReportMonthlyPoint> monthly
) {}
