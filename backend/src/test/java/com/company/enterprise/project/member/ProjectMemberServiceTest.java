package com.company.enterprise.project.member;

import com.company.enterprise.employee.entity.Employee;
import com.company.enterprise.employee.repository.EmployeeRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.member.dto.CreateProjectMemberRequest;
import com.company.enterprise.project.member.entity.ProjectMember;
import com.company.enterprise.project.member.repository.ProjectMemberRepository;
import com.company.enterprise.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    ProjectMemberRepository memberRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    ProjectMemberService memberService;

    @Test
    void addsMemberToProject() {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        Project project = mock(Project.class);
        Employee employee = mock(Employee.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(memberRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)).thenReturn(false);
        when(memberRepository.save(any(ProjectMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(project.getId()).thenReturn(projectId);
        when(employee.getId()).thenReturn(employeeId);
        when(employee.getFullName()).thenReturn("Nguyễn Văn A");

        var result = memberService.add(
                projectId,
                new CreateProjectMemberRequest(employeeId, "Backend Developer")
        );

        assertThat(result.projectId()).isEqualTo(projectId);
        assertThat(result.employeeId()).isEqualTo(employeeId);
        assertThat(result.employeeName()).isEqualTo("Nguyễn Văn A");
        assertThat(result.role()).isEqualTo("Backend Developer");

        verify(memberRepository).save(any(ProjectMember.class));
    }

    @Test
    void rejectsDuplicateMember() {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mock(Project.class)));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mock(Employee.class)));
        when(memberRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)).thenReturn(true);

        assertThatThrownBy(() -> memberService.add(
                projectId,
                new CreateProjectMemberRequest(employeeId, "Developer")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nhân viên đã là thành viên của dự án");

        verify(memberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void rejectsWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.add(
                projectId,
                new CreateProjectMemberRequest(UUID.randomUUID(), "Developer")
        ))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Không tìm thấy dự án");

        verifyNoInteractions(employeeRepository);
        verify(memberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void rejectsWhenEmployeeDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mock(Project.class)));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.add(
                projectId,
                new CreateProjectMemberRequest(employeeId, "Developer")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Không tìm thấy nhân viên");

        verify(memberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void removesMember() {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(memberRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)).thenReturn(true);

        memberService.remove(projectId, employeeId);

        verify(memberRepository).deleteByProjectIdAndEmployeeId(projectId, employeeId);
    }

    @Test
    void rejectsRemovingUnknownMember() {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(memberRepository.existsByProjectIdAndEmployeeId(projectId, employeeId)).thenReturn(false);

        assertThatThrownBy(() -> memberService.remove(projectId, employeeId))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessage("Nhân viên không phải thành viên của dự án");

        verify(memberRepository, never()).deleteByProjectIdAndEmployeeId(projectId, employeeId);
    }

    @Test
    void listsProjectMembers() {
        UUID projectId = UUID.randomUUID();
        Project project = mock(Project.class);
        Employee employee = mock(Employee.class);
        ProjectMember member = new ProjectMember(project, employee, "Developer");

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(memberRepository.findAllByProjectId(projectId)).thenReturn(java.util.List.of(member));
        when(project.getId()).thenReturn(projectId);
        when(employee.getId()).thenReturn(UUID.randomUUID());
        when(employee.getFullName()).thenReturn("Nguyễn Văn B");

        var result = memberService.findAll(projectId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).role()).isEqualTo("Developer");
        verify(memberRepository).findAllByProjectId(projectId);
    }
}
