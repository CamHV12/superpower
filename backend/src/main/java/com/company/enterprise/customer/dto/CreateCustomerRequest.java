package com.company.enterprise.customer.dto;

import com.company.enterprise.customer.entity.CustomerType;
import jakarta.validation.constraints.*;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 200) String name,
        @NotNull CustomerType type,
        @Email @Size(max = 255) String email,
        @Size(max = 30) String phone,
        @Size(max = 30) String taxCode,
        @Size(max = 150) String contactPerson,
        @Size(max = 500) String address
) {}
