package com.company.enterprise.finance.invoice;

import com.company.enterprise.finance.invoice.dto.InvoiceResponse;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    InvoiceService service;

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "invoiceNumber": "",
                                  "customerId": null,
                                  "issueDate": null,
                                  "dueDate": null,
                                  "taxAmount": -1,
                                  "discountAmount": 0,
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void createsInvoiceAndReturnsCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        var response = new InvoiceResponse(
                id, "INV-001", customerId, "ACME", null, null,
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 31),
                InvoiceStatus.DRAFT,
                new BigDecimal("1000000"), new BigDecimal("100000"),
                BigDecimal.ZERO, new BigDecimal("1100000"), BigDecimal.ZERO,
                new BigDecimal("1100000"), null, List.of());

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "invoiceNumber": "INV-001",
                                  "customerId": "%s",
                                  "issueDate": "2026-10-03",
                                  "dueDate": "2026-10-31",
                                  "taxAmount": 100000,
                                  "discountAmount": 0,
                                  "items": [
                                    {
                                      "description": "Backend development",
                                      "quantity": 1,
                                      "unitPrice": 1000000
                                    }
                                  ]
                                }
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-001"))
                .andExpect(jsonPath("$.customerName").value("ACME"))
                .andExpect(jsonPath("$.totalAmount").value(1100000));

        verify(service).create(any());
    }
}
