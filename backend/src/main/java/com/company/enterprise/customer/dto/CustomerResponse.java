package com.company.enterprise.customer.dto;

import com.company.enterprise.customer.entity.CustomerType;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id, String code, String name, CustomerType type, String email,
        String phone, String taxCode, String contactPerson, String address,
        boolean active, Instant createdAt, Instant updatedAt
) {}
