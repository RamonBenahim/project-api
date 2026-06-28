package com.challenge.project_api.service;

import com.challenge.project_api.domain.entity.Member;
import com.challenge.project_api.domain.entity.Project;
import com.challenge.project_api.domain.enums.ProjectStatus;
import com.challenge.project_api.domain.enums.RiskLevel;
import com.challenge.project_api.dto.MemberDTO;
import com.challenge.project_api.dto.ProjectRequestDTO;
import com.challenge.project_api.dto.ProjectResponseDTO;
import com.challenge.project_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberService memberService;

    public Page<ProjectResponseDTO> listProjects(String name, Pageable pageable) {
        Page<Project> projects;
        if (name != null && !name.isBlank()) {
            projects = projectRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            projects = projectRepository.findAll(pageable);
        }
        return projects.map(this::mapToResponseDTO);
    }

    public ProjectResponseDTO getProject(Long id) {
        Project project = findProjectEntity(id);
        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = new Project();
        mapToEntity(dto, project);
        
        project.setRiskLevel(calculateRiskLevel(project));
        
        project = projectRepository.save(project);
        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto) {
        Project project = findProjectEntity(id);
        
        if (project.getStatus() != dto.getStatus() && !project.getStatus().canTransitionTo(dto.getStatus())) {
            throw new IllegalArgumentException("Invalid status transition from " + project.getStatus() + " to " + dto.getStatus());
        }
        
        mapToEntity(dto, project);
        project.setRiskLevel(calculateRiskLevel(project));
        
        project = projectRepository.save(project);
        return mapToResponseDTO(project);
    }

    public void deleteProject(Long id) {
        Project project = findProjectEntity(id);
        ProjectStatus status = project.getStatus();
        if (status == ProjectStatus.INICIADO || status == ProjectStatus.EM_ANDAMENTO || status == ProjectStatus.ENCERRADO) {
            throw new IllegalStateException("Project cannot be deleted in its current status: " + status);
        }
        projectRepository.delete(project);
    }

    public ProjectResponseDTO addMemberToProject(Long projectId, Long memberId) {
        Project project = findProjectEntity(projectId);
        Member member = memberService.getMemberEntity(memberId);
        
        if (!"funcionário".equalsIgnoreCase(member.getRole()) && !"funcionario".equalsIgnoreCase(member.getRole())) {
            throw new IllegalArgumentException("Only members with role 'funcionário' can be added to a project.");
        }
        
        if (project.getMembers().size() >= 10) {
            throw new IllegalStateException("A project can have a maximum of 10 members.");
        }
        
        long activeProjectsCount = projectRepository.findAll().stream()
            .filter(p -> p.getMembers().contains(member))
            .filter(p -> p.getStatus() != ProjectStatus.ENCERRADO && p.getStatus() != ProjectStatus.CANCELADO)
            .count();
            
        if (activeProjectsCount >= 3) {
            throw new IllegalStateException("Member is already allocated to 3 active projects.");
        }
        
        project.getMembers().add(member);
        project = projectRepository.save(project);
        return mapToResponseDTO(project);
    }

    public ProjectResponseDTO removeMemberFromProject(Long projectId, Long memberId) {
        Project project = findProjectEntity(projectId);
        Member member = memberService.getMemberEntity(memberId);
        
        project.getMembers().remove(member);
        project = projectRepository.save(project);
        return mapToResponseDTO(project);
    }

    private Project findProjectEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
    }

    private void mapToEntity(ProjectRequestDTO dto, Project project) {
        project.setName(dto.getName());
        project.setStartDate(dto.getStartDate());
        project.setExpectedEndDate(dto.getExpectedEndDate());
        project.setRealEndDate(dto.getRealEndDate());
        project.setTotalBudget(dto.getTotalBudget());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setManager(memberService.getMemberEntity(dto.getManagerId()));
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setStartDate(project.getStartDate());
        dto.setExpectedEndDate(project.getExpectedEndDate());
        dto.setRealEndDate(project.getRealEndDate());
        dto.setTotalBudget(project.getTotalBudget());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setRiskLevel(project.getRiskLevel());
        
        if (project.getManager() != null) {
            MemberDTO mDto = new MemberDTO();
            mDto.setId(project.getManager().getId());
            mDto.setName(project.getManager().getName());
            mDto.setRole(project.getManager().getRole());
            dto.setManager(mDto);
        }
        
        if (project.getMembers() != null) {
            Set<MemberDTO> memberDTOs = project.getMembers().stream().map(m -> {
                MemberDTO mDto = new MemberDTO();
                mDto.setId(m.getId());
                mDto.setName(m.getName());
                mDto.setRole(m.getRole());
                return mDto;
            }).collect(Collectors.toSet());
            dto.setMembers(memberDTOs);
        } else {
            dto.setMembers(new HashSet<>());
        }
        
        return dto;
    }

    private RiskLevel calculateRiskLevel(Project project) {
        if (project.getStartDate() == null || project.getExpectedEndDate() == null) {
            return RiskLevel.BAIXO; 
        }
        long months = ChronoUnit.MONTHS.between(project.getStartDate(), project.getExpectedEndDate());
        BigDecimal budget = project.getTotalBudget();
        if (budget == null) budget = BigDecimal.ZERO;

        boolean highRiskBudget = budget.compareTo(new BigDecimal("500000")) > 0;
        boolean highRiskDuration = months > 6;
        
        if (highRiskBudget || highRiskDuration) {
            return RiskLevel.ALTO;
        }
        
        boolean mediumRiskBudget = budget.compareTo(new BigDecimal("100000")) > 0 && budget.compareTo(new BigDecimal("500000")) <= 0;
        boolean mediumRiskDuration = months > 3 && months <= 6;
        
        if (mediumRiskBudget || mediumRiskDuration) {
            return RiskLevel.MEDIO;
        }
        
        return RiskLevel.BAIXO;
    }
}
