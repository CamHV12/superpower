package com.company.enterprise.task.dto;

import com.company.enterprise.task.entity.TaskPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull UUID assigneeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate dueDate,
        @NotNull @DecimalMin(value = "0.0") BigDecimal estimatedHours,
        @NotNull TaskPriority priority
) {
}
