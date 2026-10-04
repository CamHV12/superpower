package com.company.enterprise.project.member;

import com.company.enterprise.project.member.dto.ProjectMemberResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.company.enterprise.security.JwtAuthenticationFilter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectMemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProjectMemberService memberService;

    @Test
    void returnsProjectMembers() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(memberService.findAll(projectId)).thenReturn(List.of(
                new ProjectMemberResponse(
                        UUID.randomUUID(), projectId, employeeId, "Nguyễn Văn A",
                        "Backend Developer", Instant.parse("2026-10-03T08:00:00Z")
                )
        ));

        mockMvc.perform(get("/api/v1/projects/" + projectId + "/members")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].employeeName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$[0].role").value("Backend Developer"));
    }

    @Test
    void addsProjectMember() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        when(memberService.add(eq(projectId), any()))
                .thenReturn(new ProjectMemberResponse(
                        UUID.randomUUID(), projectId, employeeId, "Nguyễn Văn A",
                        "Backend Developer", Instant.parse("2026-10-03T08:00:00Z")
                ));

        mockMvc.perform(post("/api/v1/projects/" + projectId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeId": "%s",
                                  "role": "Backend Developer"
                                }
                                """.formatted(employeeId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.role").value("Backend Developer"));
    }

    @Test
    void rejectsMemberRequestWithoutRole() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/projects/" + projectId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeId": "%s"
                                }
                                """.formatted(employeeId)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }

    @Test
    void rejectsMemberRequestWithoutEmployeeId() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/projects/" + projectId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "role": "Backend Developer"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }

    @Test
    void removesProjectMember() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        mockMvc.perform(delete(
                        "/api/v1/projects/" + projectId + "/members/" + employeeId))
                .andExpect(status().isNoContent());

        verify(memberService).remove(projectId, employeeId);
    }
}
