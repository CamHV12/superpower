package com.company.enterprise.project.repository;

import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class ProjectSpecifications {

    private ProjectSpecifications() {
    }

    public static Specification<Project> statusEquals(ProjectStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Project> priorityEquals(ProjectPriority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Project> managerEquals(UUID managerId) {
        return (root, query, cb) -> cb.equal(root.get("manager").get("id"), managerId);
    }

    public static Specification<Project> keywordContains(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
