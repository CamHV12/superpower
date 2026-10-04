package com.company.enterprise.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        Set<@NotBlank String> roles,
        boolean enabled
) {}