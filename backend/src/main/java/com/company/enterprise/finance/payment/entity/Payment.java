package com.company.enterprise.finance.payment.entity;

import com.company.enterprise.finance.invoice.entity.Invoice;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Payment() {}

    public Payment(Invoice invoice, BigDecimal amount, LocalDate paymentDate,
                   PaymentMethod method, String referenceNumber, String notes) {
        this.invoice = invoice;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public PaymentMethod getMethod() { return method; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
