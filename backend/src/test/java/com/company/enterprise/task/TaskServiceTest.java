package com.company.enterprise.task;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.dto.CreateTaskRequest;
import com.company.enterprise.task.entity.Task;
import com.company.enterprise.task.entity.TaskPriority;
import com.company.enterprise.task.entity.TaskStatus;
import com.company.enterprise.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    TaskService taskService;

    @Test
    void createsTaskWithTodoStatusAndZeroProgress() {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        Project project = mock(Project.class);
        Employee assignee = mock(Employee.class);

        var request = new CreateTaskRequest(
                "TASK-001",
                "Implement login API",
                "Build JWT login endpoint",
                assigneeId,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 10),
                new BigDecimal("8"),
                TaskPriority.HIGH
        );

        when(taskRepository.existsByCode(request.code())).thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(employeeRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.create(projectId, request);

        assertThat(result.getCode()).isEqualTo("TASK-001");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getProgress()).isZero();
        assertThat(result.getPriority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void rejectsDuplicateTaskCode() {
        UUID projectId = UUID.randomUUID();
        var request = new CreateTaskRequest(
                "TASK-001", "Duplicate", null, UUID.randomUUID(),
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                new BigDecimal("4"), TaskPriority.MEDIUM
        );

        when(taskRepository.existsByCode(request.code())).thenReturn(true);

        assertThatThrownBy(() -> taskService.create(projectId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã công việc đã tồn tại");

        verifyNoInteractions(projectRepository, employeeRepository);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void rejectsInvalidDates() {
        UUID projectId = UUID.randomUUID();
        var request = new CreateTaskRequest(
                "TASK-002", "Invalid dates", null, UUID.randomUUID(),
                LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 5),
                new BigDecimal("4"), TaskPriority.MEDIUM
        );

        when(taskRepository.existsByCode(request.code())).thenReturn(false);

        assertThatThrownBy(() -> taskService.create(projectId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ngày bắt đầu không được sau ngày kết thúc");

        verifyNoInteractions(projectRepository, employeeRepository);
    }

    @Test
    void rejectsWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        var request = new CreateTaskRequest(
                "TASK-003", "Task", null, UUID.randomUUID(),
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                new BigDecimal("4"), TaskPriority.MEDIUM
        );

        when(taskRepository.existsByCode(request.code())).thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(projectId, request))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Không tìm thấy dự án");

        verifyNoInteractions(employeeRepository);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void rejectsWhenAssigneeDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        var request = new CreateTaskRequest(
                "TASK-004", "Task", null, assigneeId,
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                new BigDecimal("4"), TaskPriority.MEDIUM
        );

        when(taskRepository.existsByCode(request.code())).thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mock(Project.class)));
        when(employeeRepository.findById(assigneeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(projectId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Không tìm thấy nhân viên được giao");

        verify(taskRepository, never()).save(any(Task.class));
    }
}
