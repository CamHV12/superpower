package com.company.enterprise.finance.invoice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.math.BigDecimal;
import com.company.enterprise.finance.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
    boolean existsByInvoiceNumber(String invoiceNumber);
    long countByStatus(com.company.enterprise.finance.invoice.entity.InvoiceStatus status);

    @Query("select coalesce(sum(i.totalAmount), 0) from Invoice i where i.issueDate between :from and :to and i.status <> com.company.enterprise.finance.invoice.entity.InvoiceStatus.CANCELLED")
    BigDecimal sumTotalAmountByIssueDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select i.id, i.invoiceNumber, i.totalAmount, i.createdAt
            from Invoice i
            order by i.createdAt desc
            """)
    List<Object[]> findRecentActivities(Pageable pageable);
}