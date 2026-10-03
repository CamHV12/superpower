package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.dto.CreateTaskRequest;
import com.company.enterprise.task.dto.UpdateTaskRequest;
import com.company.enterprise.task.entity.Task;
import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import com.company.enterprise.task.repository.TaskRepository;
import com.company.enterprise.task.repository.TaskSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional(readOnly = true)
    public Page<Task> findAll(
            UUID projectId,
            Pageable pageable,
            TaskStatus status,
            TaskPriority priority,
            UUID assigneeId,
            String keyword) {

        ensureProjectExists(projectId);

        Specification<Task> specification = Specification.where(
                TaskSpecifications.projectEquals(projectId)
        );

        if (status != null) {
            specification = specification.and(TaskSpecifications.statusEquals(status));
        }
        if (priority != null) {
            specification = specification.and(TaskSpecifications.priorityEquals(priority));
        }
        if (assigneeId != null) {
            specification = specification.and(TaskSpecifications.assigneeEquals(assigneeId));
        }
        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(TaskSpecifications.keywordContains(keyword));
        }

        return taskRepository.findAll(specification, pageable);
    }

    @Transactional
    public Task update(UUID projectId, UUID taskId, UpdateTaskRequest request) {
        validateDates(request.startDate(), request.dueDate());
        validateProgress(request.progress());

        Task task = findById(projectId, taskId);

        Employee assignee = employeeRepository.findById(request.assigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên được giao"));

        task.update(
                request.title(),
                request.description(),
                assignee,
                request.startDate(),
                request.dueDate(),
                request.estimatedHours(),
                request.actualHours(),
                request.status(),
                request.priority(),
                request.progress()
        );

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task findById(UUID projectId, UUID taskId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy công việc"));
    }

    @Transactional
    public void delete(UUID projectId, UUID taskId) {
        Task task = findById(projectId, taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public Task updateProgress(UUID projectId, UUID taskId, int progress) {
        validateProgress(progress);

        Task task = findById(projectId, taskId);
        task.setProgress(progress);

        return taskRepository.save(task);
    }

    private void ensureProjectExists(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new java.util.NoSuchElementException("Không tìm thấy dự án");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate dueDate) {
        if (startDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
    }

    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Tiến độ phải nằm trong khoảng từ 0 đến 100");
        }
    }
}
