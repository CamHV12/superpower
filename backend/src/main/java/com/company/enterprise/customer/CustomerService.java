package com.company.enterprise.customer;

import com.company.enterprise.customer.dto.*;
import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.customer.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Mã khách hàng đã tồn tại");
        }
        if (hasText(request.taxCode()) && repository.existsByTaxCode(request.taxCode())) {
            throw new IllegalArgumentException("Mã số thuế đã tồn tại");
        }

        return toResponse(repository.save(new Customer(
                request.code(), request.name(), request.type(), request.email(),
                request.phone(), request.taxCode(), request.contactPerson(), request.address()
        )));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(String keyword, Boolean active, Pageable pageable) {
        Specification<Customer> specification = Specification.where(null);

        if (hasText(keyword)) {
            specification = specification.and(CustomerSpecifications.keywordContains(keyword));
        }
        if (active != null) {
            specification = specification.and(CustomerSpecifications.activeEquals(active));
        }

        return repository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy khách hàng")));
    }

    @Transactional
    public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy khách hàng"));

        if (hasText(request.taxCode())
                && !request.taxCode().equals(customer.getTaxCode())
                && repository.existsByTaxCode(request.taxCode())) {
            throw new IllegalArgumentException("Mã số thuế đã tồn tại");
        }

        customer.update(
                request.name(), request.type(), request.email(), request.phone(),
                request.taxCode(), request.contactPerson(), request.address(), request.active()
        );

        return toResponse(repository.save(customer));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new java.util.NoSuchElementException("Không tìm thấy khách hàng");
        }
        repository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getCode(), customer.getName(), customer.getType(),
                customer.getEmail(), customer.getPhone(), customer.getTaxCode(),
                customer.getContactPerson(), customer.getAddress(), customer.isActive(),
                customer.getCreatedAt(), customer.getUpdatedAt()
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
