package com.company.enterprise.dashboard;

import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final TaskRepository taskRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardService(EmployeeRepository employeeRepository,
                            ProjectRepository projectRepository,
                            CustomerRepository customerRepository,
                            TaskRepository taskRepository,
                            InvoiceRepository invoiceRepository,
                            PaymentRepository paymentRepository,
                            ExpenseRepository expenseRepository) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
        this.taskRepository = taskRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
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

    @Transactional(readOnly = true)
    public DashboardOperationalResponse operational() {
        LocalDate today = LocalDate.now();

        List<DashboardStatusCount> projects = projectRepository.countGroupedByStatus().stream()
                .map(row -> new DashboardStatusCount(((ProjectStatus) row[0]).name(), ((Number) row[1]).longValue()))
                .sorted(Comparator.comparing(DashboardStatusCount::status))
                .toList();

        List<DashboardStatusCount> tasks = taskRepository.countGroupedByStatus().stream()
                .map(row -> new DashboardStatusCount(((com.company.enterprise.task.entity.TaskStatus) row[0]).name(), ((Number) row[1]).longValue()))
                .sorted(Comparator.comparing(DashboardStatusCount::status))
                .toList();

        List<DashboardEmployeeWorkload> employeeWorkloads = taskRepository.workloadByEmployee(today).stream()
                .map(row -> new DashboardEmployeeWorkload(
                        (java.util.UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        (java.math.BigDecimal) row[4],
                        (java.math.BigDecimal) row[5]
                ))
                .toList();

        List<DashboardCustomerKpi> customerKpis = projectRepository.kpisByCustomer().stream()
                .map(row -> new DashboardCustomerKpi(
                        (java.util.UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        (java.math.BigDecimal) row[4]
                ))
                .toList();

        return new DashboardOperationalResponse(projects, tasks, employeeWorkloads, customerKpis, recentActivities());
    }

    private List<DashboardActivity> recentActivities() {
        List<DashboardActivity> activities = new ArrayList<>();

        projectRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "PROJECT", "Dự án mới", (String) row[1], (Instant) row[2]))
                .forEach(activities::add);

        taskRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "TASK", "Task mới", (String) row[1], (Instant) row[2]))
                .forEach(activities::add);

        customerRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "CUSTOMER", "Khách hàng mới", (String) row[1], (Instant) row[2]))
                .forEach(activities::add);

        invoiceRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "INVOICE", "Hóa đơn mới", (String) row[1], (Instant) row[3]))
                .forEach(activities::add);

        paymentRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "PAYMENT", "Thanh toán mới", "Hóa đơn " + row[1] + " · " + row[2], (Instant) row[3]))
                .forEach(activities::add);

        expenseRepository.findRecentActivities().stream()
                .map(row -> new DashboardActivity((java.util.UUID) row[0], "EXPENSE", "Chi phí mới", (String) row[1] + " · " + row[2], (Instant) row[3]))
                .forEach(activities::add);

        return activities.stream()
                .sorted(Comparator.comparing(DashboardActivity::occurredAt).reversed())
                .limit(10)
                .toList();
    }
}
