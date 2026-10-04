package com.company.enterprise.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportPerformanceController {
    private final ReportPerformanceService service;

    public ReportPerformanceController(ReportPerformanceService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/performance")
    public ReportPerformanceResponse performance() {
        return service.performance();
    }
}
