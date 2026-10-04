package com.company.enterprise.dashboard;

import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock EmployeeRepository employeeRepository;
    @Mock ProjectRepository projectRepository;
    @Mock CustomerRepository customerRepository;
    @Mock TaskRepository taskRepository;

    @Test
    void returnsRealOperationalKpis() {
        when(employeeRepository.count()).thenReturn(20L);
        when(employeeRepository.countByActiveTrue()).thenReturn(17L);
        when(projectRepository.count()).thenReturn(12L);
        when(projectRepository.countByStatus(com.company.enterprise.project.entity.ProjectStatus.ACTIVE)).thenReturn(5L);
        when(customerRepository.count()).thenReturn(30L);
        when(customerRepository.countByActiveTrue()).thenReturn(26L);
        when(taskRepository.count()).thenReturn(80L);
        when(taskRepository.countOverdueOpenTasks(LocalDate.now())).thenReturn(7L);

        var result = new DashboardService(employeeRepository, projectRepository, customerRepository, taskRepository).overview();

        assertThat(result.totalEmployees()).isEqualTo(20);
        assertThat(result.activeEmployees()).isEqualTo(17);
        assertThat(result.totalProjects()).isEqualTo(12);
        assertThat(result.activeProjects()).isEqualTo(5);
        assertThat(result.totalCustomers()).isEqualTo(30);
        assertThat(result.activeCustomers()).isEqualTo(26);
        assertThat(result.totalTasks()).isEqualTo(80);
        assertThat(result.overdueTasks()).isEqualTo(7);
    }

    @Test
    void returnsProjectAndTaskStatusDistribution() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{com.company.enterprise.project.entity.ProjectStatus.ACTIVE, 5L},
                new Object[]{com.company.enterprise.project.entity.ProjectStatus.COMPLETED, 3L}
        ));
        when(taskRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{com.company.enterprise.task.entity.TaskStatus.TODO, 10L},
                new Object[]{com.company.enterprise.task.entity.TaskStatus.DONE, 7L}
        ));

        var result = new DashboardService(employeeRepository, projectRepository, customerRepository, taskRepository).operational();

        assertThat(result.projectStatuses()).extracting(DashboardStatusCount::status)
                .containsExactly("ACTIVE", "COMPLETED");
        assertThat(result.projectStatuses()).extracting(DashboardStatusCount::count)
                .containsExactly(5L, 3L);
        assertThat(result.taskStatuses()).extracting(DashboardStatusCount::status)
                .containsExactly("DONE", "TODO");
        verify(projectRepository).countGroupedByStatus();
        verify(taskRepository).countGroupedByStatus();
    }
}
