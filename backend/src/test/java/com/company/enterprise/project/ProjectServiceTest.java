package com.company.enterprise.project;

import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.dto.UpdateProjectRequest;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    ProjectService projectService;

    @Test
    void createsProjectWithDefaultDraftStatusAndZeroProgress() {
        var request = new CreateProjectRequest(
                "PRJ-001",
                "Enterprise Dashboard",
                "Quản lý doanh nghiệp tổng thể",
                UUID.randomUUID(),
                null,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("500000000"),
                ProjectPriority.HIGH
        );

        when(projectRepository.existsByCode(request.code())).thenReturn(false);
        when(employeeRepository.findById(request.managerId()))
                .thenReturn(java.util.Optional.of(mock(Employee.class)));
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Project result = projectService.create(request);

        assertThat(result.getCode()).isEqualTo("PRJ-001");
        assertThat(result.getName()).isEqualTo("Enterprise Dashboard");
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.DRAFT);
        assertThat(result.getProgress()).isZero();
        assertThat(result.getPriority()).isEqualTo(ProjectPriority.HIGH);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void rejectsDuplicateProjectCode() {
        var request = new CreateProjectRequest(
                "PRJ-001",
                "Enterprise Dashboard",
                "Mô tả",
                UUID.randomUUID(),
                null,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("500000000"),
                ProjectPriority.MEDIUM
        );

        when(projectRepository.existsByCode(request.code())).thenReturn(true);

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mã dự án đã tồn tại");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void rejectsStartDateAfterEndDate() {
        var request = new CreateProjectRequest(
                "PRJ-002",
                "Invalid Project",
                "Mô tả",
                UUID.randomUUID(),
                null,
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("100000000"),
                ProjectPriority.LOW
        );

        when(projectRepository.existsByCode(request.code())).thenReturn(false);

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ngày bắt đầu không được sau ngày kết thúc");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void rejectsProgressOutsideZeroToOneHundredWhenUpdating() {
        UUID projectId = UUID.randomUUID();
        Project project = mock(Project.class);

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));

        assertThatThrownBy(() -> projectService.updateProgress(projectId, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tiến độ phải nằm trong khoảng từ 0 đến 100");

        verify(project, never()).setProgress(anyInt());
        verify(projectRepository, never()).save(any(Project.class));
    }
    @Test
    void updatesProjectFields() {
        UUID projectId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);

        var request = new UpdateProjectRequest(
                "Updated Project",
                "Mô tả mới",
                managerId,
                null,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("750000000"),
                ProjectStatus.ACTIVE,
                ProjectPriority.URGENT,
                40
        );

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(employeeRepository.findById(managerId)).thenReturn(java.util.Optional.of(manager));
        when(projectRepository.save(project)).thenReturn(project);
        when(project.getManager()).thenReturn(manager);
        when(project.getId()).thenReturn(projectId);
        when(project.getCode()).thenReturn("PRJ-001");
        when(project.getName()).thenReturn(request.name());
        when(project.getDescription()).thenReturn(request.description());
        when(project.getStatus()).thenReturn(request.status());
        when(project.getPriority()).thenReturn(request.priority());
        when(project.getStartDate()).thenReturn(request.startDate());
        when(project.getEndDate()).thenReturn(request.endDate());
        when(project.getBudget()).thenReturn(request.budget());
        when(project.getProgress()).thenReturn(request.progress());
        when(manager.getId()).thenReturn(managerId);
        when(manager.getFullName()).thenReturn("Nguyễn Văn A");

        projectService.update(projectId, request);

        verify(project).update(
                request.name(),
                request.description(),
                manager,
                request.priority(),
                request.startDate(),
                request.endDate(),
                request.budget(),
                request.status(),
                request.progress()
        );
        verify(projectRepository).save(project);
    }

    @Test
    void rejectsUpdateWhenStartDateIsAfterEndDate() {
        UUID projectId = UUID.randomUUID();
        var request = new UpdateProjectRequest(
                "Updated Project",
                "Mô tả",
                UUID.randomUUID(),
                null,
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 10, 1),
                new BigDecimal("100000000"),
                ProjectStatus.ACTIVE,
                ProjectPriority.MEDIUM,
                20
        );

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(mock(Project.class)));

        assertThatThrownBy(() -> projectService.update(projectId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ngày bắt đầu không được sau ngày kết thúc");
    }

    @Test
    void deletesExistingProject() {
        UUID projectId = UUID.randomUUID();
        Project project = mock(Project.class);

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));

        projectService.delete(projectId);

        verify(projectRepository).delete(project);
    }

    @Test
    void rejectsDeleteWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> projectService.delete(projectId))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Không tìm thấy dự án");

        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    void findsProjectsUsingFilters() {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 20);
        UUID managerId = UUID.randomUUID();

        when(projectRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of()));

        projectService.findAll(
                pageable,
                ProjectStatus.ACTIVE,
                ProjectPriority.HIGH,
                managerId,
                "dashboard"
        );

        verify(projectRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }

}
