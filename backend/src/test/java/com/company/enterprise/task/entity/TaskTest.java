package com.company.enterprise.task.entity;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.project.entity.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void newTaskStartsAsTodoWithZeroProgress() {
        Project project = new Project();
        Employee assignee = new Employee(
                UUID.randomUUID(), "Nguyễn Văn A", "a@example.com", "0900000000", true);

        Task task = new Task(
                project,
                "TASK-001",
                "Implement login API",
                "Build JWT login endpoint",
                assignee,
                TaskPriority.HIGH,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 10),
                8
        );

        assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(task.getProgress()).isZero();
    }

    @Test
    void rejectsInvalidProgress() {
        Project project = new Project();
        Employee assignee = new Employee(
                UUID.randomUUID(), "Nguyễn Văn A", "a@example.com", "a@example.com", true);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                new Task(
                        project, "TASK-002", "Invalid", null, assignee,
                        TaskPriority.MEDIUM,
                        LocalDate.of(2026, 10, 5),
                        LocalDate.of(2026, 10, 10),
                        8
                ).setProgress(101)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tiến độ phải nằm trong khoảng từ 0 đến 100");
    }
}
