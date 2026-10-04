package com.company.enterprise.task.repository;

import com.company.enterprise.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    boolean existsByCode(String code);

    Optional<Task> findByIdAndProjectId(UUID id, UUID projectId);

    @Query("""
            select count(t)
            from Task t
            where t.dueDate < :today
              and t.status not in (com.company.enterprise.task.entity.TaskStatus.DONE,
                                   com.company.enterprise.task.entity.TaskStatus.CANCELLED)
            """)
    long countOverdueOpenTasks(@Param("today") LocalDate today);

    @Query("""
            select t.status, count(t)
            from Task t
            group by t.status
            """)
    List<Object[]> countGroupedByStatus();

    @Query("""
            select t.assignee.id,
                   t.assignee.fullName,
                   count(t),
                   sum(case when t.dueDate < :today then 1 else 0 end),
                   coalesce(sum(t.estimatedHours), 0),
                   coalesce(sum(t.actualHours), 0)
            from Task t
            where t.status not in (com.company.enterprise.task.entity.TaskStatus.DONE,
                                   com.company.enterprise.task.entity.TaskStatus.CANCELLED)
            group by t.assignee.id, t.assignee.fullName
            order by count(t) desc, t.assignee.fullName asc
            """)
    List<Object[]> workloadByEmployee(@Param("today") LocalDate today);

    @Query("""
            select t.id, t.title, t.createdAt
            from Task t
            order by t.createdAt desc
            """)
    List<Object[]> findRecentActivities();
}
