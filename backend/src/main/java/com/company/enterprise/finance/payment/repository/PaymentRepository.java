package com.company.enterprise.finance.payment.repository;

import org.springframework.data.domain.Pageable;
import com.company.enterprise.finance.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Collection;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentDate between :from and :to")
    BigDecimal sumAmountByPaymentDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") UUID invoiceId);

    List<Payment> findByInvoiceIdOrderByPaymentDateDesc(UUID invoiceId);

    List<Payment> findByPaymentDateBetweenOrderByPaymentDateAsc(LocalDate from, LocalDate to);

    @Query("""
            select p.invoice.id, coalesce(sum(p.amount), 0)
            from Payment p
            where p.invoice.id in :invoiceIds
            group by p.invoice.id
            """)
    List<Object[]> sumAmountByInvoiceIds(@Param("invoiceIds") Collection<UUID> invoiceIds);

    @Query("""
            select p.id, p.invoice.invoiceNumber, p.amount, p.createdAt
            from Payment p
            order by p.createdAt desc
            """)
    List<Object[]> findRecentActivities(Pageable pageable);
}