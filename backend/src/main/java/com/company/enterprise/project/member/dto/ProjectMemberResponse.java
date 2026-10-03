package com.company.enterprise.project.member.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID id,
        UUID projectId,
        UUID employeeId,
        String employeeName,
        String role,
        Instant joinedAt
) {
}
