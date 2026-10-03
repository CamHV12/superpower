package com.company.enterprise.employee;

import com.company.enterprise.employee.dto.CreateEmployeeRequest;
import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    public EmployeeService(EmployeeRepository employeeRepository){this.employeeRepository=employeeRepository;}

    @Transactional
    public Employee create(CreateEmployeeRequest request){
        if(employeeRepository.existsByEmail(request.email())) throw new IllegalArgumentException("Email nhân viên đã tồn tại");
        return employeeRepository.save(new Employee(null,request.fullName(),request.email(),request.phone(),true));
    }

    @Transactional(readOnly=true)
    public Page<Employee> findAll(Pageable pageable){return employeeRepository.findAll(pageable);}

    @Transactional
    public Employee update(UUID id, com.company.enterprise.employee.dto.UpdateEmployeeRequest request){
        Employee employee=employeeRepository.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy nhân viên"));
        employee.update(request.fullName(),request.phone(),request.active());
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(UUID id){
        if(!employeeRepository.existsById(id)) throw new java.util.NoSuchElementException("Không tìm thấy nhân viên");
        employeeRepository.deleteById(id);
    }
}