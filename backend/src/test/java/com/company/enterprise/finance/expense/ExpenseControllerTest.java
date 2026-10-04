package com.company.enterprise.finance.expense;

import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ExpenseService service;

    @Test
    void findsExpensesWithFilters() throws Exception {
        when(service.findAll(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/expenses")
                        .param("keyword", "office")
                        .param("category", "Văn phòng")
                        .param("status", ExpenseStatus.RECORDED.name())
                        .param("paymentMethod", PaymentMethod.BANK_TRANSFER.name())
                        .param("fromDate", "2026-10-01")
                        .param("toDate", "2026-10-31")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(service).findAll(
                any(), eq("office"), eq("Văn phòng"), eq(ExpenseStatus.RECORDED),
                eq(PaymentMethod.BANK_TRANSFER), eq(LocalDate.of(2026, 10, 1)),
                eq(LocalDate.of(2026, 10, 31)));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/expenses")
                        .contentType("application/json")
                        .content("""
                                {
                                  "category": "",
                                  "amount": 0,
                                  "expenseDate": null,
                                  "paymentMethod": null
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
