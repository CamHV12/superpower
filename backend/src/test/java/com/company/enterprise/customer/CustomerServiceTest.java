package com.company.enterprise.customer;

import com.company.enterprise.customer.dto.*;
import com.company.enterprise.customer.entity.*;
import com.company.enterprise.customer.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    @Mock CustomerRepository repository;
    @InjectMocks CustomerService service;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void createsCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "CUS-001", "Công ty ABC", CustomerType.COMPANY,
                "abc@example.com", "0900000000", "0101234567", "Nguyễn Văn A", "Hà Nội"
        );
        when(repository.existsByCode("CUS-001")).thenReturn(false);
        when(repository.existsByTaxCode("0101234567")).thenReturn(false);
        when(repository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse response = service.create(request);

        assertThat(response.code()).isEqualTo("CUS-001");
        assertThat(response.name()).isEqualTo("Công ty ABC");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsDuplicateCode() {
        when(repository.existsByCode("CUS-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateCustomerRequest(
                "CUS-001", "ABC", CustomerType.COMPANY, null, null, null, null, null
        ))).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Mã khách hàng đã tồn tại");
    }

    @Test
    void rejectsDuplicateTaxCode() {
        when(repository.existsByCode("CUS-001")).thenReturn(false);
        when(repository.existsByTaxCode("0101234567")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateCustomerRequest(
                "CUS-001", "ABC", CustomerType.COMPANY, null, null, "0101234567", null, null
        ))).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Mã số thuế đã tồn tại");
    }

    @Test
    void rejectsMissingCustomer() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
