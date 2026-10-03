package com.company.enterprise.employee;

import com.company.enterprise.employee.dto.CreateEmployeeRequest;
import com.company.enterprise.employee.dto.UpdateEmployeeRequest;
import com.company.enterprise.employee.entity.Employee;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService){this.employeeService=employeeService;}

    @GetMapping
    public Page<Employee> findAll(Pageable pageable){return employeeService.findAll(pageable);}

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody CreateEmployeeRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable UUID id,@Valid @RequestBody UpdateEmployeeRequest request){
        return employeeService.update(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){employeeService.delete(id);}
}