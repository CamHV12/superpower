package com.company.enterprise.task.entity;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.project.entity.Project;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee_id", nullable = false)
    private Employee assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskPriority priority;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "estimated_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualHours;

    @Column(nullable = false)
    private int progress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Task() {
    }

    public Task(Project project, String code, String title, String description,
                Employee assignee, TaskPriority priority,
                LocalDate startDate, LocalDate dueDate, BigDecimal estimatedHours) {
        this.project = project;
        this.code = code;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.status = TaskStatus.TODO;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.estimatedHours = estimatedHours;
        this.actualHours = BigDecimal.ZERO;
        this.progress = 0;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Project getProject() { return project; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Employee getAssignee() { return assignee; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public BigDecimal getActualHours() { return actualHours; }
    public int getProgress() { return progress; }

    public void setProgress(int progress) {
        validateProgress(progress);
        this.progress = progress;
    }

    public void update(String title, String description, Employee assignee,
                       LocalDate startDate, LocalDate dueDate,
                       BigDecimal estimatedHours, BigDecimal actualHours,
                       TaskStatus status, TaskPriority priority, int progress) {
        validateProgress(progress);
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.status = status;
        this.priority = priority;
        this.progress = progress;
    }

    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Tiến độ phải nằm trong khoảng từ 0 đến 100");
        }
    }
}
