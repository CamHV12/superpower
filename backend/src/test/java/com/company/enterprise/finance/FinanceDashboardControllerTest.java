package com.company.enterprise.finance;

import com.company.enterprise.finance.dto.FinanceSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinanceDashboardController.class)
@Import(FinanceDashboardController.class)
class FinanceDashboardControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean FinanceDashboardService service;

    @Test
    void returnsFinanceSummary() throws Exception {
        when(service.summary()).thenReturn(new FinanceSummaryResponse(
                new BigDecimal("3000000"),
                new BigDecimal("1500000"),
                new BigDecimal("1500000"),
                new BigDecimal("400000"),
                new BigDecimal("1100000"),
                1,
                new BigDecimal("1500000")
        ));

        mockMvc.perform(get("/api/v1/finance/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvoiced").value(3000000))
                .andExpect(jsonPath("$.totalPaid").value(1500000))
                .andExpect(jsonPath("$.totalReceivable").value(1500000))
                .andExpect(jsonPath("$.totalExpense").value(400000))
                .andExpect(jsonPath("$.netCashFlow").value(1100000))
                .andExpect(jsonPath("$.overdueInvoices").value(1))
                .andExpect(jsonPath("$.overdueAmount").value(1500000));
    }
}
