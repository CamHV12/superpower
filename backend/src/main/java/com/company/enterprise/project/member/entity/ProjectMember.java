package com.company.enterprise.project.member.entity;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.project.entity.Project;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project_members",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_project_members_project_employee",
               columnNames = {"project_id", "employee_id"}))
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected ProjectMember() {
    }

    public ProjectMember(Project project, Employee employee, String role) {
        this.project = project;
        this.employee = employee;
        this.role = role;
    }

    @PrePersist
    void onCreate() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public Project getProject() { return project; }
    public Employee getEmployee() { return employee; }
    public String getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}
