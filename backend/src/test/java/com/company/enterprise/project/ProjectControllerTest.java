package com.company.enterprise.project;

import com.company.enterprise.project.dto.ProjectResponse;
import com.company.enterprise.project.dto.UpdateProjectRequest;
import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProjectService projectService;

    @Test
    void returnsPagedProjects() throws Exception {
        ProjectResponse response = new ProjectResponse(
                UUID.randomUUID(),
                "PRJ-001",
                "Enterprise Dashboard",
                "Quản lý doanh nghiệp tổng thể",
                UUID.randomUUID(),
                "Nguyễn Văn A",
                ProjectStatus.DRAFT,
                ProjectPriority.HIGH,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("500000000"),
                0,
                Instant.parse("2026-10-03T08:00:00Z"),
                Instant.parse("2026-10-03T08:00:00Z")
        );

        when(projectService.findAll(any(PageRequest.class), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("PRJ-001"))
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.content[0].progress").value(0));
    }
    @Test
    void updatesProject() throws Exception {
        UUID id = UUID.randomUUID();
        ProjectResponse response = new ProjectResponse(
                id, "PRJ-001", "Updated Project", "Mô tả mới",
                UUID.randomUUID(), "Nguyễn Văn A",
                ProjectStatus.ACTIVE, ProjectPriority.URGENT,
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 12, 31),
                new BigDecimal("750000000"), 40,
                Instant.parse("2026-10-03T08:00:00Z"),
                Instant.parse("2026-10-03T08:00:00Z")
        );

        when(projectService.update(any(UUID.class), any(UpdateProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/projects/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Project",
                                  "description": "Mô tả mới",
                                  "managerId": "00000000-0000-0000-0000-000000000001",
                                  "startDate": "2026-10-05",
                                  "endDate": "2026-12-31",
                                  "budget": 750000000,
                                  "status": "ACTIVE",
                                  "priority": "URGENT",
                                  "progress": 40
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.progress").value(40));
    }

    @Test
    void filtersProjectsByStatusPriorityManagerAndKeyword() throws Exception {
        UUID managerId = UUID.randomUUID();

        when(projectService.findAll(
                any(PageRequest.class),
                eq(ProjectStatus.ACTIVE),
                eq(ProjectPriority.HIGH),
                eq(managerId),
                eq("dashboard")
        )).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/projects")
                        .param("status", "ACTIVE")
                        .param("priority", "HIGH")
                        .param("managerId", managerId.toString())
                        .param("keyword", "dashboard")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
