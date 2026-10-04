package com.company.enterprise.report;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportOperationalController {
    private final ReportOperationalService service;

    public ReportOperationalController(ReportOperationalService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/operational")
    public ReportOperationalResponse operational() {
        return service.operational();
    }
}
