package com.company.enterprise.project.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProjectMemberRequest(
        UUID employeeId,
        @NotBlank @Size(max = 100) String role
) {
}
