package com.company.enterprise.finance;

import com.company.enterprise.finance.dto.FinanceSummaryResponse;
import com.company.enterprise.finance.dto.FinanceMonthlyResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finance")
public class FinanceDashboardController {
    private final FinanceDashboardService service;

    public FinanceDashboardController(FinanceDashboardService service) {
        this.service = service;
    }

    @GetMapping("/monthly")
    public List<FinanceMonthlyResponse> monthly(@RequestParam(defaultValue = "6") int months) {
        return service.monthly(months);
    }

    @GetMapping("/summary")
    public FinanceSummaryResponse summary() {
        return service.summary();
    }
}
