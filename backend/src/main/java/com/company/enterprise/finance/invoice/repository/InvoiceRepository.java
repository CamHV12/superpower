package com.company.enterprise.finance.invoice.repository;

import com.company.enterprise.finance.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
    boolean existsByInvoiceNumber(String invoiceNumber);
}
