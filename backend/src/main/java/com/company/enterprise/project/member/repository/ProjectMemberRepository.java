package com.company.enterprise.project.member.repository;

import com.company.enterprise.project.member.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    List<ProjectMember> findAllByProjectId(UUID projectId);
    boolean existsByProjectIdAndEmployeeId(UUID projectId, UUID employeeId);
    void deleteByProjectIdAndEmployeeId(UUID projectId, UUID employeeId);
}
