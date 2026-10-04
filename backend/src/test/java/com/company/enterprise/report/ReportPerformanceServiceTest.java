package com.company.enterprise.report;

import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import com.company.enterprise.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportPerformanceServiceTest {
    @Mock ProjectRepository projectRepository;
    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;

    @InjectMocks ReportPerformanceService service;

    @Test
    void buildsProjectAndCustomerPerformance() {
        UUID projectId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(projectRepository.performanceReport()).thenReturn(List.of(new Object[]{
                projectId, "P-001", "Website", "ACME", "ACTIVE", 75,
                new BigDecimal("10000"), 4L, 3L, 1L,
                new BigDecimal("40"), new BigDecimal("35")
        }));
        when(projectRepository.customerPerformanceReport()).thenReturn(List.of(new Object[]{
                customerId, "C-001", "ACME", true, 2L, 1L, new BigDecimal("15000")
        }));
        when(invoiceRepository.sumAmountsByCustomer()).thenReturn(List.of(new Object[]{
                customerId, new BigDecimal("12000")
        }));
        when(paymentRepository.sumAmountsByCustomer()).thenReturn(List.of(new Object[]{
                customerId, new BigDecimal("9000")
        }));

        ReportPerformanceResponse result = service.performance();

        assertThat(result.projects()).hasSize(1);
        assertThat(result.projects().getFirst().completedTasks()).isEqualTo(3);
        assertThat(result.projects().getFirst().progress()).isEqualTo(75);
        assertThat(result.customers().getFirst().invoicedAmount()).isEqualByComparingTo("12000");
        assertThat(result.customers().getFirst().paidAmount()).isEqualByComparingTo("9000");
        assertThat(result.customers().getFirst().receivableAmount()).isEqualByComparingTo("3000");
    }
}
