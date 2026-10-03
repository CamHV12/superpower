package com.company.enterprise.project;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.dto.ProjectResponse;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository) { this.projectRepository = projectRepository; this.employeeRepository = employeeRepository; }

    @Transactional
    public Project create(CreateProjectRequest request) {
        if (projectRepository.existsByCode(request.code())) throw new IllegalArgumentException("Mã dự án đã tồn tại");
        validateDates(request);
        Employee manager = employeeRepository.findById(request.managerId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người quản lý dự án"));
        return projectRepository.save(new Project(request.code(), request.name(), request.description(), manager, request.priority(), request.startDate(), request.endDate(), request.budget()));
    }

    @Transactional
    public ProjectResponse createResponse(CreateProjectRequest request) { return toResponse(create(request)); }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(Pageable pageable) { return projectRepository.findAll(pageable).map(this::toResponse); }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID projectId) {
        return toResponse(projectRepository.findById(projectId).orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án")));
    }

    @Transactional
    public void updateProgress(UUID projectId, int progress) {
        validateProgress(progress);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));
        project.setProgress(progress);
        projectRepository.save(project);
    }

    @Transactional
    public ProjectResponse updateProgressAndReturn(UUID projectId, int progress) { updateProgress(projectId, progress); return findById(projectId); }

    private ProjectResponse toResponse(Project project) {
        Employee manager = project.getManager();
        return new ProjectResponse(project.getId(), project.getCode(), project.getName(), project.getDescription(), manager.getId(), manager.getFullName(), project.getStatus(), project.getPriority(), project.getStartDate(), project.getEndDate(), project.getBudget(), project.getProgress(), project.getCreatedAt(), project.getUpdatedAt());
    }

    private void validateDates(CreateProjectRequest request) {
        if (request.startDate().isAfter(request.endDate())) throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
    }
    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) throw new IllegalArgumentException("Tiến độ phải nằm trong khoảng từ 0 đến 100");
    }
}
