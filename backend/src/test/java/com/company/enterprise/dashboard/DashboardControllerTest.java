package com.company.enterprise.dashboard;

import org.junit.jupiter.api.Test;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.company.enterprise.security.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean DashboardService service;

    @Test
    void returnsOperationalKpis() throws Exception {
        when(service.overview()).thenReturn(new DashboardOverviewResponse(
                20, 17, 12, 5, 30, 26, 80, 7));

        mockMvc.perform(get("/api/v1/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(20))
                .andExpect(jsonPath("$.activeEmployees").value(17))
                .andExpect(jsonPath("$.totalProjects").value(12))
                .andExpect(jsonPath("$.activeProjects").value(5))
                .andExpect(jsonPath("$.totalCustomers").value(30))
                .andExpect(jsonPath("$.activeCustomers").value(26))
                .andExpect(jsonPath("$.totalTasks").value(80))
                .andExpect(jsonPath("$.overdueTasks").value(7));
    }

    @Test
    void returnsOperationalAnalytics() throws Exception {
        when(service.operational()).thenReturn(new DashboardOperationalResponse(
                List.of(new DashboardStatusCount("ACTIVE", 2)),
                List.of(new DashboardStatusCount("TODO", 3)),
                List.of(new DashboardEmployeeWorkload(
                        java.util.UUID.randomUUID(), "Nguyen Van A", 3, 1,
                        new java.math.BigDecimal("20"), new java.math.BigDecimal("15"))),
                List.of(new DashboardCustomerKpi(
                        java.util.UUID.randomUUID(), "ABC Company", 2, 1,
                        new java.math.BigDecimal("100000000"))),
                List.of(new DashboardActivity(
                        java.util.UUID.randomUUID(), "PROJECT", "Dự án mới", "Project Alpha",
                        java.time.Instant.parse("2026-10-04T04:00:00Z")))
        ));

        mockMvc.perform(get("/api/v1/dashboard/operational"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectStatuses[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.taskStatuses[0].count").value(3))
                .andExpect(jsonPath("$.employeeWorkloads[0].employeeName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.customerKpis[0].customerName").value("ABC Company"))
                .andExpect(jsonPath("$.recentActivities[0].description").value("Project Alpha"));
    }
}
