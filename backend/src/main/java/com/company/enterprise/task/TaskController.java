package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.task.dto.CreateTaskRequest;
import com.company.enterprise.task.dto.TaskResponse;
import com.company.enterprise.task.dto.UpdateTaskRequest;
import com.company.enterprise.task.entity.Task;
import jakarta.validation.Valid;
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

    @GetMapping("/{taskId}")
    public TaskResponse findById(@PathVariable UUID taskId) {
        return toResponse(taskService.findById(taskId));
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return toResponse(taskService.update(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID taskId) {
        taskService.delete(taskId);
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
