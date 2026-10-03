package com.company.enterprise.finance.payment.dto;

import com.company.enterprise.finance.payment.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID invoiceId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull LocalDate paymentDate,
        @NotNull PaymentMethod method,
        @Size(max = 100) String referenceNumber,
        @Size(max = 1000) String notes
) {}
