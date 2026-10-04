package com.company.enterprise.report;

import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.entity.TaskStatus;
import com.company.enterprise.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock ExpenseRepository expenseRepository;
    @Mock ProjectRepository projectRepository;
    @Mock TaskRepository taskRepository;
    @Mock CustomerRepository customerRepository;

    @InjectMocks ReportService service;

    @Test
    void buildsSummaryAndMonthlyReport() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 28);

        when(invoiceRepository.sumTotalAmountByIssueDateBetween(from, to)).thenReturn(new BigDecimal("1000"));
        when(paymentRepository.sumAmountByPaymentDateBetween(from, to)).thenReturn(new BigDecimal("700"));
        when(expenseRepository.sumRecordedAmountByExpenseDateBetween(from, to)).thenReturn(new BigDecimal("200"));
        when(invoiceRepository.countByIssueDateBetween(from, to)).thenReturn(5L);
        when(invoiceRepository.countByIssueDateBetweenAndStatus(from, to, InvoiceStatus.OVERDUE)).thenReturn(1L);
        when(invoiceRepository.countByIssueDateBetweenAndStatus(from, to, InvoiceStatus.PAID)).thenReturn(3L);
        when(projectRepository.count()).thenReturn(8L);
        when(projectRepository.countByStatus(ProjectStatus.ACTIVE)).thenReturn(4L);
        when(taskRepository.count()).thenReturn(30L);
        when(taskRepository.countByStatus(TaskStatus.DONE)).thenReturn(20L);
        when(customerRepository.count()).thenReturn(6L);
        when(customerRepository.countByActiveTrue()).thenReturn(5L);

        when(invoiceRepository.sumTotalAmountByIssueDateBetween(from, from)).thenReturn(new BigDecimal("400"));
        when(paymentRepository.sumAmountByPaymentDateBetween(from, from)).thenReturn(new BigDecimal("300"));
        when(expenseRepository.sumRecordedAmountByExpenseDateBetween(from, from)).thenReturn(new BigDecimal("100"));
        when(invoiceRepository.sumTotalAmountByIssueDateBetween(LocalDate.of(2026, 2, 1), to)).thenReturn(new BigDecimal("600"));
        when(paymentRepository.sumAmountByPaymentDateBetween(LocalDate.of(2026, 2, 1), to)).thenReturn(new BigDecimal("400"));
        when(expenseRepository.sumRecordedAmountByExpenseDateBetween(LocalDate.of(2026, 2, 1), to)).thenReturn(new BigDecimal("100"));

        ReportResponse result = service.summary(from, to);

        assertThat(result.summary().invoicedAmount()).isEqualByComparingTo("1000");
        assertThat(result.summary().paidAmount()).isEqualByComparingTo("700");
        assertThat(result.summary().expenseAmount()).isEqualByComparingTo("200");
        assertThat(result.summary().receivableAmount()).isEqualByComparingTo("300");
        assertThat(result.summary().netCashFlow()).isEqualByComparingTo("500");
        assertThat(result.summary().invoiceCount()).isEqualTo(5);
        assertThat(result.summary().paidInvoiceCount()).isEqualTo(3);
        assertThat(result.monthly()).hasSize(2);
    }

    @Test
    void rejectsInvalidDateRange() {
        LocalDate from = LocalDate.of(2026, 2, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.summary(from, to))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
