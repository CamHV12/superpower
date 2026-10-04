package com.company.enterprise.finance;

import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
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
import static org.mockito.Mockito.eq;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class FinanceDashboardServiceTest {
    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock ExpenseRepository expenseRepository;

    @Test
    void calculatesMonthlyPaidExpenseAndNetCashFlow() {
        FinanceDashboardService service = new FinanceDashboardService(invoiceRepository, paymentRepository, expenseRepository);

        var payment = org.mockito.Mockito.mock(com.company.enterprise.finance.payment.entity.Payment.class);
        when(payment.getPaymentDate()).thenReturn(LocalDate.now().withDayOfMonth(1));
        when(payment.getAmount()).thenReturn(new BigDecimal("1000000"));
        when(paymentRepository.findByPaymentDateBetweenOrderByPaymentDateAsc(any(), any()))
                .thenReturn(List.of(payment));

        var expense = org.mockito.Mockito.mock(com.company.enterprise.finance.expense.entity.Expense.class);
        when(expense.getExpenseDate()).thenReturn(LocalDate.now().withDayOfMonth(1));
        when(expense.getAmount()).thenReturn(new BigDecimal("400000"));
        when(expenseRepository.findByStatusAndExpenseDateBetweenOrderByExpenseDateAsc(
                eq(ExpenseStatus.RECORDED), any(), any()))
                .thenReturn(List.of(expense));

        var result = service.monthly(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).paidAmount()).isEqualByComparingTo("1000000");
        assertThat(result.get(0).expenseAmount()).isEqualByComparingTo("400000");
        assertThat(result.get(0).netCashFlow()).isEqualByComparingTo("600000");
    }

    @Test
    void calculatesFinanceSummaryFromInvoicesAndPayments() {
        FinanceDashboardService service = new FinanceDashboardService(invoiceRepository, paymentRepository, expenseRepository);

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
        when(paymentRepository.sumAmountByInvoiceIds(any())).thenReturn(List.of(
                new Object[]{paidId, new BigDecimal("1000000")},
                new Object[]{openId, new BigDecimal("500000")}
        ));
        when(expenseRepository.sumAmountByStatus(eq(ExpenseStatus.RECORDED)))
                .thenReturn(new BigDecimal("400000"));

        var result = service.summary();

        assertThat(result.totalInvoiced()).isEqualByComparingTo("3000000");
        assertThat(result.totalPaid()).isEqualByComparingTo("1500000");
        assertThat(result.totalReceivable()).isEqualByComparingTo("1500000");
        assertThat(result.totalExpense()).isEqualByComparingTo("400000");
        assertThat(result.netCashFlow()).isEqualByComparingTo("1100000");
        assertThat(result.overdueInvoices()).isEqualTo(1);
        assertThat(result.overdueAmount()).isEqualByComparingTo("1500000");
    }
}
