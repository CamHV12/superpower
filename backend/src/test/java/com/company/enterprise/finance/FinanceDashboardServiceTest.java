package com.company.enterprise.finance;

import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceDashboardServiceTest {
    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;

    @Test
    void calculatesFinanceSummaryFromInvoicesAndPayments() {
        FinanceDashboardService service = new FinanceDashboardService(invoiceRepository, paymentRepository);

        Invoice paidInvoice = org.mockito.Mockito.mock(Invoice.class);
        Invoice openInvoice = org.mockito.Mockito.mock(Invoice.class);
        UUID paidId = UUID.randomUUID();
        UUID openId = UUID.randomUUID();

        when(paidInvoice.getId()).thenReturn(paidId);
        when(paidInvoice.getStatus()).thenReturn(InvoiceStatus.PAID);
        when(paidInvoice.getTotalAmount()).thenReturn(new BigDecimal("1000000"));

        when(openInvoice.getId()).thenReturn(openId);
        when(openInvoice.getStatus()).thenReturn(InvoiceStatus.SENT);
        when(openInvoice.getTotalAmount()).thenReturn(new BigDecimal("2000000"));
        when(openInvoice.getDueDate()).thenReturn(LocalDate.now().minusDays(1));

        when(invoiceRepository.findAll()).thenReturn(List.of(paidInvoice, openInvoice));
        when(paymentRepository.sumAmountByInvoiceId(paidId)).thenReturn(new BigDecimal("1000000"));
        when(paymentRepository.sumAmountByInvoiceId(openId)).thenReturn(new BigDecimal("500000"));

        var result = service.summary();

        assertThat(result.totalInvoiced()).isEqualByComparingTo("3000000");
        assertThat(result.totalPaid()).isEqualByComparingTo("1500000");
        assertThat(result.totalReceivable()).isEqualByComparingTo("1500000");
        assertThat(result.overdueInvoices()).isEqualTo(1);
        assertThat(result.overdueAmount()).isEqualByComparingTo("1500000");
    }
}
