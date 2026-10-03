package com.company.enterprise.finance.invoice;

import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.finance.invoice.dto.CreateInvoiceItemRequest;
import com.company.enterprise.finance.invoice.dto.CreateInvoiceRequest;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock InvoiceRepository invoiceRepository;
    @Mock CustomerRepository customerRepository;
    @Mock ProjectRepository projectRepository;
    @Mock PaymentRepository paymentRepository;
    @InjectMocks InvoiceService service;

    @Test
    void createsInvoiceAndCalculatesTotals() {
        UUID customerId = UUID.randomUUID();
        Customer customer = mock(Customer.class);
        var request = new CreateInvoiceRequest(
                "INV-001", customerId, null,
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 31),
                new BigDecimal("100000"), new BigDecimal("50000"), "Ghi chú",
                List.of(
                        new CreateInvoiceItemRequest("Backend", new BigDecimal("2"), new BigDecimal("1000000")),
                        new CreateInvoiceItemRequest("Frontend", BigDecimal.ONE, new BigDecimal("500000"))
                ));

        when(invoiceRepository.existsByInvoiceNumber("INV-001")).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.sumAmountByInvoiceId(any())).thenReturn(BigDecimal.ZERO);

        var result = service.create(request);

        assertThat(result.subtotal()).isEqualByComparingTo("2500000");
        assertThat(result.taxAmount()).isEqualByComparingTo("100000");
        assertThat(result.discountAmount()).isEqualByComparingTo("50000");
        assertThat(result.totalAmount()).isEqualByComparingTo("2550000");
        assertThat(result.paidAmount()).isEqualByComparingTo("0");
        assertThat(result.remainingAmount()).isEqualByComparingTo("2550000");
        assertThat(result.items()).hasSize(2);
        assertThat(result.status()).hasToString("DRAFT");
    }

    @Test
    void findsInvoicesWithFilters() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        UUID customerId = UUID.randomUUID();

        when(invoiceRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        var result = service.findAll(
                pageable,
                "ACME",
                com.company.enterprise.finance.invoice.entity.InvoiceStatus.SENT,
                customerId,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31));

        assertThat(result.getContent()).isEmpty();
        verify(invoiceRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable));
    }

    @Test
    void rejectsDuplicateInvoiceNumber() {
        var request = new CreateInvoiceRequest(
                "INV-001", UUID.randomUUID(), null,
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 31),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                List.of(new CreateInvoiceItemRequest("Service", BigDecimal.ONE, new BigDecimal("1000"))));

        when(invoiceRepository.existsByInvoiceNumber("INV-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Số hóa đơn đã tồn tại");

        verifyNoInteractions(customerRepository);
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void rejectsInvoiceWithUnknownCustomer() {
        UUID customerId = UUID.randomUUID();
        var request = new CreateInvoiceRequest(
                "INV-002", customerId, null,
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 31),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                List.of(new CreateInvoiceItemRequest("Service", BigDecimal.ONE, new BigDecimal("1000"))));

        when(invoiceRepository.existsByInvoiceNumber("INV-002")).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Không tìm thấy khách hàng");
    }

    @Test
    void rejectsProjectBelongingToAnotherCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID projectCustomerId = UUID.randomUUID();
        Customer customer = mock(Customer.class);
        Customer projectCustomer = mock(Customer.class);
        Project project = mock(Project.class);

        when(customer.getId()).thenReturn(customerId);
        when(projectCustomer.getId()).thenReturn(projectCustomerId);
        when(project.getCustomer()).thenReturn(projectCustomer);

        var request = new CreateInvoiceRequest(
                "INV-003", customerId, UUID.randomUUID(),
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 31),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                List.of(new CreateInvoiceItemRequest("Service", BigDecimal.ONE, new BigDecimal("1000"))));

        when(invoiceRepository.existsByInvoiceNumber("INV-003")).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(projectRepository.findById(request.projectId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dự án không thuộc khách hàng của hóa đơn");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void rejectsIssueDateAfterDueDate() {
        var request = new CreateInvoiceRequest(
                "INV-004", UUID.randomUUID(), null,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 10, 31),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                List.of(new CreateInvoiceItemRequest("Service", BigDecimal.ONE, new BigDecimal("1000"))));

        when(invoiceRepository.existsByInvoiceNumber("INV-004")).thenReturn(false);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ngày phát hành không được sau ngày đến hạn");
    }
}
