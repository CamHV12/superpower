package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.dto.CreateTaskRequest;
import com.company.enterprise.task.entity.Task;
import com.company.enterprise.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Task create(UUID projectId, CreateTaskRequest request) {
        if (taskRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Mã công việc đã tồn tại");
        }

        validateDates(request.startDate(), request.dueDate());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));

        Employee assignee = employeeRepository.findById(request.assigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên được giao"));

        Task task = new Task(
                project,
                request.code(),
                request.title(),
                request.description(),
                assignee,
                request.priority(),
                request.startDate(),
                request.dueDate(),
                request.estimatedHours()
        );

        return taskRepository.save(task);
    }

    private void validateDates(LocalDate startDate, LocalDate dueDate) {
        if (startDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
    }
}
