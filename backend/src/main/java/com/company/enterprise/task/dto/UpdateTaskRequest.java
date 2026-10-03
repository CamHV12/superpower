package com.company.enterprise.task.dto;

import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskRequest(
        @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull UUID assigneeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate dueDate,
        @NotNull @DecimalMin(value = "0.0") BigDecimal estimatedHours,
        @NotNull @DecimalMin(value = "0.0") BigDecimal actualHours,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority,
        @NotNull Integer progress
) {
}
