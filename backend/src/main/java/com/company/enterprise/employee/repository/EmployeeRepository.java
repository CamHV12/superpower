package com.company.enterprise.employee.repository;

import com.company.enterprise.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByEmail(String email);
    long countByActiveTrue();
}
