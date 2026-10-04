package com.company.enterprise.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public DashboardOverviewResponse overview() {
        return service.overview();
    }

    @GetMapping("/operational")
    public DashboardOperationalResponse operational() {
        return service.operational();
    }
}
