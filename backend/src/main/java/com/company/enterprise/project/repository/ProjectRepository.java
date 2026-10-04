package com.company.enterprise.project.repository;

import org.springframework.data.domain.Pageable;
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

    @Query("""
            select p.id, p.code, p.name,
                   coalesce(p.customer.name, 'Không có khách hàng'),
                   p.status, p.progress, coalesce(p.budget, 0),
                   count(t),
                   sum(case when t.status = com.company.enterprise.task.entity.TaskStatus.DONE then 1 else 0 end),
                   sum(case when t.dueDate < current_date and t.status not in (com.company.enterprise.task.entity.TaskStatus.DONE, com.company.enterprise.task.entity.TaskStatus.CANCELLED) then 1 else 0 end),
                   coalesce(sum(t.estimatedHours), 0),
                   coalesce(sum(t.actualHours), 0)
            from Project p
            left join com.company.enterprise.task.entity.Task t on t.project = p
            group by p.id, p.code, p.name, p.customer.name, p.status, p.progress, p.budget
            order by p.progress desc, p.name asc
            """)
    List<Object[]> performanceReport();

    @Query("""
            select c.id, c.code, c.name, c.active,
                   count(p),
                   sum(case when p.status = com.company.enterprise.project.entity.ProjectStatus.ACTIVE then 1 else 0 end),
                   coalesce(sum(p.budget), 0)
            from com.company.enterprise.customer.entity.Customer c
            left join Project p on p.customer = c
            group by c.id, c.code, c.name, c.active
            order by count(p) desc, c.name asc
            """)
    List<Object[]> customerPerformanceReport();

    @Query("""
            select p.id, p.name, p.createdAt
            from Project p
            order by p.createdAt desc
            """)
    List<Object[]> findRecentActivities(Pageable pageable);
}