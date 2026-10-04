package com.company.enterprise.dashboard;

import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DashboardService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final TaskRepository taskRepository;

    public DashboardService(EmployeeRepository employeeRepository,
                            ProjectRepository projectRepository,
                            CustomerRepository customerRepository,
                            TaskRepository taskRepository) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public DashboardOverviewResponse overview() {
        return new DashboardOverviewResponse(
                employeeRepository.count(),
                employeeRepository.countByActiveTrue(),
                projectRepository.count(),
                projectRepository.countByStatus(ProjectStatus.ACTIVE),
                customerRepository.count(),
                customerRepository.countByActiveTrue(),
                taskRepository.count(),
                taskRepository.countOverdueOpenTasks(LocalDate.now())
        );
    }
}
