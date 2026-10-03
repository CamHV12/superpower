package com.company.enterprise.task;

import com.company.enterprise.task.dto.TaskResponse;
import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        UUID taskId = UUID.randomUUID();

        var response = new TaskResponse(
                taskId,
                projectId,
                "TASK-001",
                "Implement login API",
                "Build JWT login endpoint",
                assigneeId,
                "Nguyễn Văn A",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 10),
                new BigDecimal("8"),
                BigDecimal.ZERO,
                0,
                Instant.parse("2026-10-03T08:00:00Z"),
                Instant.parse("2026-10-03T08:00:00Z")
        );

        when(taskService.create(eq(projectId), any())).thenReturn(
                mockTaskResponseSource(response)
        );

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

    private com.company.enterprise.task.entity.Task mockTaskResponseSource(TaskResponse response) {
        var task = mock(com.company.enterprise.task.entity.Task.class);
        var project = mock(com.company.enterprise.project.entity.Project.class);
        var employee = mock(com.company.enterprise.employee.entity.Employee.class);

        when(task.getId()).thenReturn(response.id());
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(response.projectId());
        when(task.getCode()).thenReturn(response.code());
        when(task.getTitle()).thenReturn(response.title());
        when(task.getDescription()).thenReturn(response.description());
        when(task.getAssignee()).thenReturn(employee);
        when(employee.getId()).thenReturn(response.assigneeId());
        when(employee.getFullName()).thenReturn(response.assigneeName());
        when(task.getStatus()).thenReturn(response.status());
        when(task.getPriority()).thenReturn(response.priority());
        when(task.getStartDate()).thenReturn(response.startDate());
        when(task.getDueDate()).thenReturn(response.dueDate());
        when(task.getEstimatedHours()).thenReturn(response.estimatedHours());
        when(task.getActualHours()).thenReturn(response.actualHours());
        when(task.getProgress()).thenReturn(response.progress());
        when(task.getCreatedAt()).thenReturn(response.createdAt());
        when(task.getUpdatedAt()).thenReturn(response.updatedAt());

        return task;
    }
}
