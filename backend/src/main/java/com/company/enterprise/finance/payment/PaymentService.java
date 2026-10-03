package com.company.enterprise.finance.payment;

import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.dto.CreatePaymentRequest;
import com.company.enterprise.finance.payment.dto.PaymentResponse;
import com.company.enterprise.finance.payment.entity.Payment;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hóa đơn"));

        if (invoice.getStatus() == com.company.enterprise.finance.invoice.entity.InvoiceStatus.CANCELLED) {
            throw new IllegalArgumentException("Không thể thanh toán hóa đơn đã hủy");
        }

        BigDecimal paid = paymentRepository.sumAmountByInvoiceId(invoice.getId());
        BigDecimal remaining = invoice.getTotalAmount().subtract(paid);

        if (request.amount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Số tiền thanh toán vượt quá số tiền còn phải thu");
        }

        Payment payment = new Payment(invoice, request.amount(), request.paymentDate(),
                request.method(), request.referenceNumber(), request.notes());

        Payment saved = paymentRepository.save(payment);
        BigDecimal newPaid = paid.add(request.amount());
        invoice.updatePaymentStatus(newPaid);
        invoiceRepository.save(invoice);

        return toResponse(saved);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoice().getId(),
                payment.getInvoice().getInvoiceNumber(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getMethod(),
                payment.getReferenceNumber(),
                payment.getNotes(),
                payment.getCreatedAt()
        );
    }
}
