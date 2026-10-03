package com.company.enterprise.finance.invoice;

import com.company.enterprise.finance.invoice.dto.CreateInvoiceRequest;
import com.company.enterprise.finance.invoice.dto.InvoiceResponse;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {
    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Page<InvoiceResponse> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            Pageable pageable) {
        return service.findAll(pageable, keyword, status, customerId, fromDate, toDate);
    }

    @GetMapping("/{id}")
    public InvoiceResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
