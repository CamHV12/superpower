package com.company.enterprise.finance.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateInvoiceItemRequest(
        @NotBlank @Size(max = 500) String description,
        @NotNull @DecimalMin(value = "0.0001") BigDecimal quantity,
        @NotNull @DecimalMin(value = "0.0") BigDecimal unitPrice
) {}
