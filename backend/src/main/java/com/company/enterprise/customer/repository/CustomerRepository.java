package com.company.enterprise.customer.repository;

import com.company.enterprise.customer.entity.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    boolean existsByCode(String code);
    boolean existsByTaxCode(String taxCode);
    long countByActiveTrue();

    @Query("""
            select c.id, c.name, c.createdAt
            from Customer c
            order by c.createdAt desc
            """)
    List<Object[]> findRecentActivities(Pageable pageable);
}