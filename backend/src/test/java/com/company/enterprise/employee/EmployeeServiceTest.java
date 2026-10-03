package com.company.enterprise.employee;

import com.company.enterprise.employee.dto.CreateEmployeeRequest;
import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmployeeServiceTest {
    @Mock EmployeeRepository employeeRepository;
    @InjectMocks EmployeeService employeeService;

    @Test
    void createsEmployee() {
        var request = new CreateEmployeeRequest("Nguyễn Văn An","an@example.com","0900000000");
        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.create(request);

        assertThat(result.getFullName()).isEqualTo("Nguyễn Văn An");
        assertThat(result.getEmail()).isEqualTo("an@example.com");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void rejectsDuplicateEmail() {
        var request = new CreateEmployeeRequest("Nguyễn Văn An","an@example.com","0900000000");
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email nhân viên đã tồn tại");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void listsEmployeesWithPagination() {
        Page<Employee> page = new PageImpl<>(java.util.List.of(
                new Employee(UUID.randomUUID(),"An","an@example.com","0900000000",true)
        ));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = employeeService.findAll(PageRequest.of(0,20));

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
