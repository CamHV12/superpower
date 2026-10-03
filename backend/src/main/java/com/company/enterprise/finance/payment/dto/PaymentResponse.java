package com.company.enterprise.finance.payment.dto;

import com.company.enterprise.finance.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        String invoiceNumber,
        BigDecimal amount,
        LocalDate paymentDate,
        PaymentMethod method,
        String referenceNumber,
        String notes,
        Instant createdAt
) {}
