package com.company.enterprise.finance.expense.entity;

import com.company.enterprise.finance.payment.entity.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(length = 200)
    private String vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Expense() {}

    public Expense(String category, BigDecimal amount, LocalDate expenseDate,
                   String vendor, PaymentMethod paymentMethod, String notes) {
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.vendor = vendor;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.status = ExpenseStatus.RECORDED;
    }

    public UUID getId() { return id; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public String getVendor() { return vendor; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getNotes() { return notes; }
    public ExpenseStatus getStatus() { return status; }

    public void cancel() {
        if (status == ExpenseStatus.CANCELLED) {
            throw new IllegalStateException("Khoản chi đã được hủy");
        }
        status = ExpenseStatus.CANCELLED;
    }

    @PrePersist
    void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }
}
