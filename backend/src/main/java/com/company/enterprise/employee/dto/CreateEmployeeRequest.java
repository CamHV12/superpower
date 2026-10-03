package com.company.enterprise.employee.dto;

import jakarta.validation.constraints.*;

public record CreateEmployeeRequest(
        @NotBlank @Size(max=150) String fullName,
        @NotBlank @Email @Size(max=255) String email,
        @Size(max=30) String phone
) {}