package com.company.enterprise.employee.dto;

import jakarta.validation.constraints.*;

public record UpdateEmployeeRequest(
        @NotBlank @Size(max=150) String fullName,
        @Size(max=30) String phone,
        boolean active
) {}