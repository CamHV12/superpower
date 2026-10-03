package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.task.dto.CreateTaskRequest;
import com.company.enterprise.task.dto.TaskResponse;
import com.company.enterprise.task.dto.UpdateTaskRequest;
import com.company.enterprise.task.entity.Task;
import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        return toResponse(taskService.create(projectId, request));
    }

    @GetMapping
    public Page<TaskResponse> findAll(
            @PathVariable UUID projectId,
            Pageable pageable,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) String keyword) {
        return taskService.findAll(projectId, pageable, status, priority, assigneeId, keyword)
                .map(this::toResponse);
    }

    @GetMapping("/{taskId}")
    public TaskResponse findById(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return toResponse(taskService.findById(projectId, taskId));
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return toResponse(taskService.update(projectId, taskId, request));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        taskService.delete(projectId, taskId);
    }

    @PatchMapping("/{taskId}/progress")
    public TaskResponse updateProgress(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestParam int progress) {
        return toResponse(taskService.updateProgress(projectId, taskId, progress));
    }

    private TaskResponse toResponse(Task task) {
        Employee assignee = task.getAssignee();

        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getCode(),
                task.getTitle(),
                task.getDescription(),
                assignee.getId(),
                assignee.getFullName(),
                task.getStatus(),
                task.getPriority(),
                task.getStartDate(),
                task.getDueDate(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getProgress(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
