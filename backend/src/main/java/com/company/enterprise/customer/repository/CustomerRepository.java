package com.company.enterprise.customer.repository;

import com.company.enterprise.customer.entity.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    boolean existsByCode(String code);
    boolean existsByTaxCode(String taxCode);
}
