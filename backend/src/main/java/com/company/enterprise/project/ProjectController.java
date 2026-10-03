package com.company.enterprise.project;

import com.company.enterprise.project.dto.CreateProjectRequest;
import com.company.enterprise.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable UUID id) {
        return projectService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createResponse(request));
    }

    @PatchMapping("/{id}/progress")
    public ProjectResponse updateProgress(
            @PathVariable UUID id,
            @RequestParam int progress) {
        return projectService.updateProgressAndReturn(id, progress);
    }
}
