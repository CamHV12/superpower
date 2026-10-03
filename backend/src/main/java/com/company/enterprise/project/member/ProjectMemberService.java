package com.company.enterprise.project.member;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.project.member.dto.CreateProjectMemberRequest;
import com.company.enterprise.project.member.dto.ProjectMemberResponse;
import com.company.enterprise.project.member.entity.ProjectMember;
import com.company.enterprise.project.member.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectMemberService(
            ProjectMemberRepository memberRepository,
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> findAll(UUID projectId) {
        ensureProjectExists(projectId);
        return memberRepository.findAllByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProjectMemberResponse add(UUID projectId, CreateProjectMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        if (memberRepository.existsByProjectIdAndEmployeeId(projectId, request.employeeId())) {
            throw new IllegalArgumentException("Nhân viên đã là thành viên của dự án");
        }

        ProjectMember member = new ProjectMember(project, employee, request.role());
        return toResponse(memberRepository.save(member));
    }

    @Transactional
    public void remove(UUID projectId, UUID employeeId) {
        ensureProjectExists(projectId);

        if (!memberRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)) {
            throw new java.util.NoSuchElementException("Nhân viên không phải thành viên của dự án");
        }

        memberRepository.deleteByProjectIdAndEmployeeId(projectId, employeeId);
    }

    private void ensureProjectExists(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new java.util.NoSuchElementException("Không tìm thấy dự án");
        }
    }

    private ProjectMemberResponse toResponse(ProjectMember member) {
        return new ProjectMemberResponse(
                member.getId(),
                member.getProject().getId(),
                member.getEmployee().getId(),
                member.getEmployee().getFullName(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
