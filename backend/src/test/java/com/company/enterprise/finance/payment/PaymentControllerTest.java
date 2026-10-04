package com.company.enterprise.finance.payment;

import com.company.enterprise.finance.payment.dto.PaymentResponse;
import com.company.enterprise.finance.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.company.enterprise.security.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean PaymentService service;

    @Test
    void returnsInvoicePaymentHistory() throws Exception {
        UUID invoiceId = UUID.randomUUID();

        when(service.findByInvoiceId(invoiceId)).thenReturn(List.of(
                new PaymentResponse(
                        UUID.randomUUID(), invoiceId, "INV-001",
                        new BigDecimal("500000"), LocalDate.of(2026, 10, 3),
                        PaymentMethod.BANK_TRANSFER, "TX-001", null, null)
        ));

        mockMvc.perform(get("/api/v1/payments").param("invoiceId", invoiceId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-001"))
                .andExpect(jsonPath("$[0].amount").value(500000));
    }
}
