package com.company.enterprise.project;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository,
                          EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Project create(CreateProjectRequest request) {
        if (projectRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Mã dự án đã tồn tại");
        }

        validateDates(request);

        Employee manager = employeeRepository.findById(request.managerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người quản lý dự án"));

        Project project = new Project(
                request.code(),
                request.name(),
                request.description(),
                manager,
                request.priority(),
                request.startDate(),
                request.endDate(),
                request.budget()
        );

        return projectRepository.save(project);
    }

    @Transactional
    public void updateProgress(UUID projectId, int progress) {
        validateProgress(progress);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));

        project.setProgress(progress);
        projectRepository.save(project);
    }

    private void validateDates(CreateProjectRequest request) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
    }

    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Tiến độ phải nằm trong khoảng từ 0 đến 100");
        }
    }
}
