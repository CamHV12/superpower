package com.company.enterprise.project.repository;

import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {
    boolean existsByCode(String code);
    long countByStatus(ProjectStatus status);

    @Query("""
            select p.status, count(p)
            from Project p
            group by p.status
            """)
    List<Object[]> countGroupedByStatus();

    @Query("""
            select p.customer.id,
                   p.customer.name,
                   count(p),
                   sum(case when p.status = com.company.enterprise.project.entity.ProjectStatus.ACTIVE then 1 else 0 end),
                   coalesce(sum(p.budget), 0)
            from Project p
            where p.customer is not null
            group by p.customer.id, p.customer.name
            order by count(p) desc, p.customer.name asc
            """)
    List<Object[]> kpisByCustomer();
}
