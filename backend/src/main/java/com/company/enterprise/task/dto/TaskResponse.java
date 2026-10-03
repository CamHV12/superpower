package com.company.enterprise.task.dto;

import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String code,
        String title,
        String description,
        UUID assigneeId,
        String assigneeName,
        TaskStatus status,
        TaskPriority priority,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours,
        BigDecimal actualHours,
        int progress,
        Instant createdAt,
        Instant updatedAt
) {
}
