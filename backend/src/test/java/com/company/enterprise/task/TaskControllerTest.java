package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.task.dto.TaskResponse;
import com.company.enterprise.task.entity.Task;
import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    @Test
    void createsTask() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        Task task = mockTask(projectId, assigneeId, "TASK-001", "Implement login API");

        when(taskService.create(eq(projectId), any())).thenReturn(task);

        mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "TASK-001",
                                  "title": "Implement login API",
                                  "description": "Build JWT login endpoint",
                                  "assigneeId": "%s",
                                  "startDate": "2026-10-05",
                                  "dueDate": "2026-10-10",
                                  "estimatedHours": 8,
                                  "priority": "HIGH"
                                }
                                """.formatted(assigneeId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TASK-001"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.progress").value(0));
    }

    @Test
    void listsTasksWithFiltersAndPagination() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        Task task = mockTask(projectId, assigneeId, "TASK-001", "Implement login API");

        when(taskService.findAll(
                eq(projectId),
                any(Pageable.class),
                eq(TaskStatus.IN_PROGRESS),
                eq(TaskPriority.HIGH),
                eq(assigneeId),
                eq("login")
        )).thenReturn(new PageImpl<>(List.of(task), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks")
                        .param("status", "IN_PROGRESS")
                        .param("priority", "HIGH")
                        .param("assigneeId", assigneeId.toString())
                        .param("keyword", "login")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("TASK-001"))
                .andExpect(jsonPath("$.content[0].title").value("Implement login API"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void updatesTaskProgress() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task task = mockTask(projectId, assigneeId, "TASK-001", "Implement login API");
        when(task.getId()).thenReturn(taskId);
        when(task.getProgress()).thenReturn(75);

        when(taskService.updateProgress(projectId, taskId, 75)).thenReturn(task);

        mockMvc.perform(patch("/api/v1/projects/" + projectId + "/tasks/" + taskId + "/progress")
                        .param("progress", "75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(75));

        verify(taskService).updateProgress(projectId, taskId, 75);
    }

    @Test
    void rejectsTaskRequestWithoutTitle() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "TASK-001",
                                  "assigneeId": "%s",
                                  "startDate": "2026-10-05",
                                  "dueDate": "2026-10-10",
                                  "estimatedHours": 8,
                                  "priority": "HIGH"
                                }
                                """.formatted(assigneeId)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    private Task mockTask(UUID projectId, UUID assigneeId, String code, String title) {
        var task = mock(Task.class);
        var project = mock(Project.class);
        var employee = mock(Employee.class);

        when(task.getId()).thenReturn(UUID.randomUUID());
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(projectId);
        when(task.getCode()).thenReturn(code);
        when(task.getTitle()).thenReturn(title);
        when(task.getDescription()).thenReturn("Build JWT login endpoint");
        when(task.getAssignee()).thenReturn(employee);
        when(employee.getId()).thenReturn(assigneeId);
        when(employee.getFullName()).thenReturn("Nguyễn Văn A");
        when(task.getStatus()).thenReturn(TaskStatus.TODO);
        when(task.getPriority()).thenReturn(TaskPriority.HIGH);
        when(task.getStartDate()).thenReturn(LocalDate.of(2026, 10, 5));
        when(task.getDueDate()).thenReturn(LocalDate.of(2026, 10, 10));
        when(task.getEstimatedHours()).thenReturn(new BigDecimal("8"));
        when(task.getActualHours()).thenReturn(BigDecimal.ZERO);
        when(task.getProgress()).thenReturn(0);
        when(task.getCreatedAt()).thenReturn(Instant.parse("2026-10-03T08:00:00Z"));
        when(task.getUpdatedAt()).thenReturn(Instant.parse("2026-10-03T08:00:00Z"));

        return task;
    }
}
