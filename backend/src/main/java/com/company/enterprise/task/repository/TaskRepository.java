package com.company.enterprise.task.repository;

import com.company.enterprise.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
    long countOverdueOpenTasks(@Param("today") java.time.LocalDate today);
}
