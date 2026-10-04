package com.company.enterprise.finance.payment;

import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.dto.CreatePaymentRequest;
import com.company.enterprise.finance.payment.entity.Payment;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock PaymentRepository paymentRepository;
    @Mock InvoiceRepository invoiceRepository;
    @InjectMocks PaymentService service;

    @Test
    void createsPaymentAndMarksInvoicePaid() {
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoice.getInvoiceNumber()).thenReturn("INV-001");
        when(invoice.getTotalAmount()).thenReturn(new BigDecimal("1000000"));
        when(invoice.getStatus()).thenReturn(InvoiceStatus.SENT);
        when(invoice.getIssueDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.sumAmountByInvoiceId(invoiceId)).thenReturn(new BigDecimal("600000"));

        Payment payment = mock(Payment.class);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new CreatePaymentRequest(
                invoiceId, new BigDecimal("400000"), LocalDate.of(2026, 10, 3),
                PaymentMethod.BANK_TRANSFER, "BANK-001", null);

        service.create(request);

        verify(invoice).updatePaymentStatus(new BigDecimal("1000000"));
        verify(invoiceRepository).save(invoice);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void rejectsPaymentAboveRemainingAmount() {
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();

        when(invoice.getId()).thenReturn(invoiceId);
        when(invoice.getTotalAmount()).thenReturn(new BigDecimal("1000000"));
        when(invoice.getStatus()).thenReturn(InvoiceStatus.SENT);
        when(invoice.getIssueDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.sumAmountByInvoiceId(invoiceId)).thenReturn(new BigDecimal("800000"));

        var request = new CreatePaymentRequest(
                invoiceId, new BigDecimal("300000"), LocalDate.of(2026, 10, 3),
                PaymentMethod.BANK_TRANSFER, null, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Số tiền thanh toán vượt quá số tiền còn phải thu");

        verify(paymentRepository, never()).save(any());
        verify(invoiceRepository, never()).save(invoice);
    }

    @Test
    void rejectsPaymentBeforeInvoiceIssueDate() {
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();

        when(invoice.getStatus()).thenReturn(InvoiceStatus.SENT);
        when(invoice.getIssueDate()).thenReturn(LocalDate.of(2026, 10, 3));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        var request = new CreatePaymentRequest(
                invoiceId, new BigDecimal("100000"), LocalDate.of(2026, 10, 2),
                PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ngày thanh toán không được trước ngày phát hành hóa đơn");

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void rejectsPaymentForCancelledInvoice() {
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();

        when(invoice.getStatus()).thenReturn(InvoiceStatus.CANCELLED);
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        var request = new CreatePaymentRequest(
                invoiceId, new BigDecimal("100000"), LocalDate.of(2026, 10, 3),
                PaymentMethod.CASH, null, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Không thể thanh toán hóa đơn đã hủy");

        verifyNoInteractions(paymentRepository);
    }
}
