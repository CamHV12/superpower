package com.company.enterprise.finance.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotBlank @Size(max = 50) String invoiceNumber,
        @NotNull UUID customerId,
        UUID projectId,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate dueDate,
        @NotNull @DecimalMin("0.0") BigDecimal taxAmount,
        @NotNull @DecimalMin("0.0") BigDecimal discountAmount,
        @Size(max = 2000) String notes,
        @NotEmpty List<@Valid CreateInvoiceItemRequest> items
) {}
