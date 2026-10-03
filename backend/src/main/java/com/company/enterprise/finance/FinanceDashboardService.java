package com.company.enterprise.finance;

import com.company.enterprise.finance.dto.FinanceSummaryResponse;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceDashboardService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public FinanceDashboardService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse summary() {
        List<Invoice> invoices = invoiceRepository.findAll();

        BigDecimal totalInvoiced = invoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.CANCELLED)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = invoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.CANCELLED)
                .map(invoice -> paymentRepository.sumAmountByInvoiceId(invoice.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceivable = totalInvoiced.subtract(totalPaid).max(BigDecimal.ZERO);

        LocalDate today = LocalDate.now();
        List<Invoice> overdue = invoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID)
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.CANCELLED)
                .filter(invoice -> invoice.getDueDate().isBefore(today))
                .toList();

        BigDecimal overdueAmount = overdue.stream()
                .map(invoice -> invoice.getTotalAmount()
                        .subtract(paymentRepository.sumAmountByInvoiceId(invoice.getId()))
                        .max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceSummaryResponse(
                totalInvoiced,
                totalPaid,
                totalReceivable,
                overdue.size(),
                overdueAmount
        );
    }
}
