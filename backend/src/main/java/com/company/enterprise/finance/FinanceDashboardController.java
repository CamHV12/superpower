package com.company.enterprise.finance;

import com.company.enterprise.finance.dto.FinanceSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finance")
public class FinanceDashboardController {
    private final FinanceDashboardService service;

    public FinanceDashboardController(FinanceDashboardService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public FinanceSummaryResponse summary() {
        return service.summary();
    }
}
