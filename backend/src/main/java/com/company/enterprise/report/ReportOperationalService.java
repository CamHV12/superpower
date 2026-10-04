package com.company.enterprise.report;

import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportOperationalService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ReportOperationalService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public ReportOperationalResponse operational() {
        LocalDate today = LocalDate.now();

        List<ReportOperationalResponse.StatusCount> projects = projectRepository.countGroupedByStatus()
                .stream()
                .map(row -> new ReportOperationalResponse.StatusCount(
                        String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        List<ReportOperationalResponse.StatusCount> tasks = taskRepository.countGroupedByStatus()
                .stream()
                .map(row -> new ReportOperationalResponse.StatusCount(
                        String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        List<ReportOperationalResponse.EmployeePerformance> employees =
                taskRepository.performanceByEmployee(today).stream()
                        .map(row -> new ReportOperationalResponse.EmployeePerformance(
                                (java.util.UUID) row[0],
                                (String) row[1],
                                ((Number) row[2]).longValue(),
                                ((Number) row[3]).longValue(),
                                ((Number) row[4]).longValue(),
                                (java.math.BigDecimal) row[5],
                                (java.math.BigDecimal) row[6]
                        ))
                        .toList();

        List<ReportOperationalResponse.CustomerPerformance> customers =
                projectRepository.kpisByCustomer().stream()
                        .map(row -> new ReportOperationalResponse.CustomerPerformance(
                                (java.util.UUID) row[0],
                                (String) row[1],
                                ((Number) row[2]).longValue(),
                                ((Number) row[3]).longValue(),
                                (java.math.BigDecimal) row[4]
                        ))
                        .toList();

        return new ReportOperationalResponse(projects, tasks, employees, customers);
    }
}
