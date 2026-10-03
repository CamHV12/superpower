package com.company.enterprise.task.repository;

import com.company.enterprise.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    boolean existsByCode(String code);
}
