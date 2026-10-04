package com.company.enterprise.finance;

import com.company.enterprise.finance.dto.FinanceMonthlyResponse;
import com.company.enterprise.finance.dto.FinanceSummaryResponse;
import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.entity.Payment;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FinanceDashboardService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    public FinanceDashboardService(InvoiceRepository invoiceRepository,
                                   PaymentRepository paymentRepository,
                                   ExpenseRepository expenseRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<FinanceMonthlyResponse> monthly(int months) {
        int safeMonths = Math.max(1, Math.min(months, 12));
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1).minusMonths(safeMonths - 1L);

        List<Payment> payments = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateAsc(start, end);
        List<Expense> expenses = expenseRepository.findByStatusAndExpenseDateBetweenOrderByExpenseDateAsc(
                ExpenseStatus.RECORDED, start, end);

        Map<String, BigDecimal> paidByMonth = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByMonth = new LinkedHashMap<>();

        for (int i = 0; i < safeMonths; i++) {
            String month = start.plusMonths(i).toString().substring(0, 7);
            paidByMonth.put(month, BigDecimal.ZERO);
            expenseByMonth.put(month, BigDecimal.ZERO);
        }

        payments.forEach(payment -> {
            String month = payment.getPaymentDate().toString().substring(0, 7);
            if (paidByMonth.containsKey(month)) {
                paidByMonth.computeIfPresent(month, (key, value) -> value.add(payment.getAmount()));
            }
        });

        expenses.forEach(expense -> {
            String month = expense.getExpenseDate().toString().substring(0, 7);
            if (expenseByMonth.containsKey(month)) {
                expenseByMonth.computeIfPresent(month, (key, value) -> value.add(expense.getAmount()));
            }
        });

        return paidByMonth.keySet().stream()
                .map(month -> {
                    BigDecimal paid = paidByMonth.get(month);
                    BigDecimal expense = expenseByMonth.get(month);
                    return new FinanceMonthlyResponse(month, paid, expense, paid.subtract(expense));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceSummaryResponse summary() {
        List<Invoice> activeInvoices = invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.CANCELLED)
                .toList();

        BigDecimal totalInvoiced = activeInvoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<UUID, BigDecimal> paidByInvoice = paymentTotals(activeInvoices);

        BigDecimal totalPaid = paidByInvoice.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceivable = totalInvoiced.subtract(totalPaid).max(BigDecimal.ZERO);
        BigDecimal totalExpense = expenseRepository.sumAmountByStatus(ExpenseStatus.RECORDED);
        BigDecimal netCashFlow = totalPaid.subtract(totalExpense);

        LocalDate today = LocalDate.now();
        List<Invoice> overdue = activeInvoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID)
                .filter(invoice -> invoice.getDueDate().isBefore(today))
                .toList();

        BigDecimal overdueAmount = overdue.stream()
                .map(invoice -> invoice.getTotalAmount()
                        .subtract(paidByInvoice.getOrDefault(invoice.getId(), BigDecimal.ZERO))
                        .max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceSummaryResponse(
                totalInvoiced,
                totalPaid,
                totalReceivable,
                totalExpense,
                netCashFlow,
                overdue.size(),
                overdueAmount
        );
    }

    private Map<UUID, BigDecimal> paymentTotals(List<Invoice> invoices) {
        if (invoices.isEmpty()) {
            return Map.of();
        }

        List<UUID> invoiceIds = invoices.stream().map(Invoice::getId).toList();
        Map<UUID, BigDecimal> totals = new LinkedHashMap<>();

        paymentRepository.sumAmountByInvoiceIds(invoiceIds).forEach(row -> {
            UUID invoiceId = (UUID) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            totals.put(invoiceId, amount);
        });

        return totals;
    }
}
