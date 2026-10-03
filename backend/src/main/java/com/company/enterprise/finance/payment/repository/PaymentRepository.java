package com.company.enterprise.finance.payment.repository;

import com.company.enterprise.finance.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    BigDecimal sumAmountByInvoiceId(UUID invoiceId);
}
