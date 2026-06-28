package com.challenge.project_api.controller;

import com.challenge.project_api.dto.ProjectRequestDTO;
import com.challenge.project_api.dto.ProjectResponseDTO;
import com.challenge.project_api.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO createProject(@Valid @RequestBody ProjectRequestDTO dto) {
        return projectService.createProject(dto);
    }

    @GetMapping
    public Page<ProjectResponseDTO> listProjects(@RequestParam(required = false) String name, Pageable pageable) {
        return projectService.listProjects(name, pageable);
    }

    @GetMapping("/{id}")
    public ProjectResponseDTO getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PutMapping("/{id}")
    public ProjectResponseDTO updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO dto) {
        return projectService.updateProject(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{projectId}/members/{memberId}")
    public ProjectResponseDTO addMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        return projectService.addMemberToProject(projectId, memberId);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ProjectResponseDTO removeMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        return projectService.removeMemberFromProject(projectId, memberId);
    }
}
