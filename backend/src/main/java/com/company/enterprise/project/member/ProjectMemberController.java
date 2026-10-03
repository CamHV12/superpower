package com.company.enterprise.project.member;

import com.company.enterprise.project.member.dto.CreateProjectMemberRequest;
import com.company.enterprise.project.member.dto.ProjectMemberResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService memberService;

    public ProjectMemberController(ProjectMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<ProjectMemberResponse> findAll(@PathVariable UUID projectId) {
        return memberService.findAll(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse add(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectMemberRequest request) {
        return memberService.add(projectId, request);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @PathVariable UUID projectId,
            @PathVariable UUID employeeId) {
        memberService.remove(projectId, employeeId);
    }
}
