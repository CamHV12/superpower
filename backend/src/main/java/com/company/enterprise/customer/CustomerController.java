package com.company.enterprise.customer;

import com.company.enterprise.customer.dto.*;
import com.company.enterprise.project.ProjectService;
import com.company.enterprise.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;
    private final ProjectService projectService;

    public CustomerController(CustomerService service, ProjectService projectService) {
        this.service = service;
        this.projectService = projectService;
    }

    @GetMapping
    public Page<CustomerResponse> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return service.findAll(keyword, active, pageable);
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/projects")
    public Page<ProjectResponse> projects(@PathVariable UUID id, Pageable pageable) {
        service.findById(id);
        return projectService.findAll(pageable, null, null, null, id, null);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
