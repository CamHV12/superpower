package com.company.enterprise.project;

import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.entity.ProjectPriority;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectService projectService;

    @Test
    void createsProjectWithDefaultDraftStatusAndZeroProgress() {
        var request = new CreateProjectRequest(
                "PRJ-001",
                "Enterprise Dashboard",
                "Quản lý doanh nghiệp tổng thể",
                UUID.randomUUID(),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("500000000"),
                ProjectPriority.HIGH
        );

        when(projectRepository.existsByCode(request.code())).thenReturn(false);
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
}
