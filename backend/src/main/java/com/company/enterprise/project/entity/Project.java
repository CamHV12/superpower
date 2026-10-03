package com.company.enterprise.project.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private com.company.enterprise.employee.entity.Employee manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectPriority priority;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal budget;

    @Column(nullable = false)
    private int progress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Project() {}

    public Project(String code, String name, String description,
                   com.company.enterprise.employee.entity.Employee manager,
                   ProjectPriority priority, LocalDate startDate, LocalDate endDate,
                   BigDecimal budget) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.status = ProjectStatus.DRAFT;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
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
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public com.company.enterprise.employee.entity.Employee getManager() { return manager; }
    public ProjectStatus getStatus() { return status; }
    public ProjectPriority getPriority() { return priority; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getBudget() { return budget; }
    public int getProgress() { return progress; }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void update(String name, String description,
                       com.company.enterprise.employee.entity.Employee manager,
                       ProjectPriority priority, LocalDate startDate, LocalDate endDate,
                       BigDecimal budget, ProjectStatus status, int progress) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.status = status;
        this.progress = progress;
    }
}
