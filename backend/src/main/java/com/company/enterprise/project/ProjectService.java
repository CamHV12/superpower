package com.company.enterprise.project;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.dto.ProjectResponse;
import com.company.enterprise.project.dto.UpdateProjectRequest;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.project.repository.ProjectSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository, CustomerRepository customerRepository) { this.projectRepository = projectRepository; this.employeeRepository = employeeRepository; this.customerRepository = customerRepository; }

    @Transactional
    public Project create(CreateProjectRequest request) {
        if (projectRepository.existsByCode(request.code())) throw new IllegalArgumentException("Mã dự án đã tồn tại");
        validateDates(request);
        Employee manager = employeeRepository.findById(request.managerId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người quản lý dự án"));
        Customer customer = findCustomer(request.customerId());
        return projectRepository.save(new Project(request.code(), request.name(), request.description(), manager, customer, request.priority(), request.startDate(), request.endDate(), request.budget()));
    }

    @Transactional
    public ProjectResponse createResponse(CreateProjectRequest request) { return toResponse(create(request)); }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(Pageable pageable) { return projectRepository.findAll(pageable).map(this::toResponse); }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(
            Pageable pageable,
            com.company.enterprise.project.entity.ProjectStatus status,
            com.company.enterprise.project.entity.ProjectPriority priority,
            UUID managerId,
            UUID customerId,
            String keyword) {
        Specification<Project> specification = Specification.where(null);

        if (status != null) {
            specification = specification.and(ProjectSpecifications.statusEquals(status));
        }
        if (priority != null) {
            specification = specification.and(ProjectSpecifications.priorityEquals(priority));
        }
        if (managerId != null) {
            specification = specification.and(ProjectSpecifications.managerEquals(managerId));
        }
        if (customerId != null) specification = specification.and(ProjectSpecifications.customerEquals(customerId));
        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(ProjectSpecifications.keywordContains(keyword));
        }

        return projectRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID projectId) {
        return toResponse(projectRepository.findById(projectId).orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án")));
    }

    @Transactional
    public ProjectResponse update(UUID projectId, UpdateProjectRequest request) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        validateProgress(request.progress());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));
        Employee manager = employeeRepository.findById(request.managerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người quản lý dự án"));
        Customer customer = findCustomer(request.customerId());

        project.update(
                request.name(), request.description(), manager, customer,
                request.priority(), request.startDate(), request.endDate(),
                request.budget(), request.status(), request.progress()
        );

        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy dự án"));

        projectRepository.delete(project);
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
        Customer customer = project.getCustomer();
        return new ProjectResponse(project.getId(), project.getCode(), project.getName(), project.getDescription(), manager.getId(), manager.getFullName(), customer == null ? null : customer.getId(), customer == null ? null : customer.getName(), project.getStatus(), project.getPriority(), project.getStartDate(), project.getEndDate(), project.getBudget(), project.getProgress(), project.getCreatedAt(), project.getUpdatedAt());
    }

    private Customer findCustomer(UUID customerId) {
        if (customerId == null) return null;
        return customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));
    }

    private void validateDates(CreateProjectRequest request) {
        if (request.startDate().isAfter(request.endDate())) throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
    }
    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) throw new IllegalArgumentException("Tiến độ phải nằm trong khoảng từ 0 đến 100");
    }
}
