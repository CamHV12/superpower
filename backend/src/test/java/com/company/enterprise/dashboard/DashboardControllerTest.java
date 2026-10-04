package com.company.enterprise.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {
    @Autowired MockMvc mockMvc;
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
}
