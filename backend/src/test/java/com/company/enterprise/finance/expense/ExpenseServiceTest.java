package com.company.enterprise.finance.expense;

import com.company.enterprise.finance.expense.dto.CreateExpenseRequest;
import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock ExpenseRepository repository;
    @InjectMocks ExpenseService service;

    @Test
    void createsExpense() {
        when(repository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new CreateExpenseRequest(
                "Văn phòng", new BigDecimal("2500000"), LocalDate.of(2026, 10, 3),
                "Nhà cung cấp A", PaymentMethod.BANK_TRANSFER, "Tiền thuê văn phòng"));

        assertThat(result.amount()).isEqualByComparingTo("2500000");
        assertThat(result.category()).isEqualTo("Văn phòng");
        assertThat(result.status()).hasToString("RECORDED");
    }

    @Test
    void rejectsUnknownExpenseOnDelete() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Không tìm thấy khoản chi");

        verify(repository, never()).delete(any());
    }
}
