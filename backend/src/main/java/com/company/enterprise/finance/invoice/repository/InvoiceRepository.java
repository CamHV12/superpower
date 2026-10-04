package com.company.enterprise.finance.invoice.repository;

import com.company.enterprise.finance.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("""
            select i.id, i.invoiceNumber, i.totalAmount, i.createdAt
            from Invoice i
            order by i.createdAt desc
            """)
    List<Object[]> findRecentActivities();
}
