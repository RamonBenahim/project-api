package com.challenge.project_api.service;

import com.challenge.project_api.domain.entity.Member;
import com.challenge.project_api.domain.entity.Project;
import com.challenge.project_api.domain.enums.ProjectStatus;
import com.challenge.project_api.domain.enums.RiskLevel;
import com.challenge.project_api.dto.ProjectRequestDTO;
import com.challenge.project_api.dto.ProjectResponseDTO;
import com.challenge.project_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ProjectService projectService;

    private Member manager;

    @BeforeEach
    void setUp() {
        manager = new Member();
        manager.setId(1L);
        manager.setName("Manager");
        manager.setRole("gerente");
    }

    @Test
    void createProject_LowRisk() {
        ProjectRequestDTO req = new ProjectRequestDTO();
        req.setName("Test Low Risk");
        req.setStartDate(LocalDate.now());
        req.setExpectedEndDate(LocalDate.now().plusMonths(2));
        req.setTotalBudget(new BigDecimal("50000"));
        req.setManagerId(1L);
        req.setStatus(ProjectStatus.EM_ANALISE);

        when(memberService.getMemberEntity(1L)).thenReturn(manager);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> {
            Project p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProjectResponseDTO res = projectService.createProject(req);

        assertNotNull(res);
        assertEquals(RiskLevel.BAIXO, res.getRiskLevel());
        assertEquals("Test Low Risk", res.getName());
    }

    @Test
    void createProject_HighRisk() {
        ProjectRequestDTO req = new ProjectRequestDTO();
        req.setName("Test High Risk");
        req.setStartDate(LocalDate.now());
        req.setExpectedEndDate(LocalDate.now().plusMonths(7));
        req.setTotalBudget(new BigDecimal("100000"));
        req.setManagerId(1L);
        req.setStatus(ProjectStatus.EM_ANALISE);

        when(memberService.getMemberEntity(1L)).thenReturn(manager);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        ProjectResponseDTO res = projectService.createProject(req);

        assertEquals(RiskLevel.ALTO, res.getRiskLevel());
    }

    @Test
    void deleteProject_ThrowsIfInvalidStatus() {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.INICIADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class, () -> projectService.deleteProject(1L));
        verify(projectRepository, never()).delete(any());
    }

    @Test
    void deleteProject_Success() {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.EM_ANALISE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void addMember_Success() {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.INICIADO);
        project.setMembers(new HashSet<>());

        Member emp = new Member();
        emp.setId(2L);
        emp.setName("Employee");
        emp.setRole("funcionário");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getMemberEntity(2L)).thenReturn(emp);
        when(projectRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProjectResponseDTO res = projectService.addMemberToProject(1L, 2L);

        assertEquals(1, res.getMembers().size());
    }

    @Test
    void addMember_FailsIfNotFuncionario() {
        Project project = new Project();
        project.setId(1L);

        Member ger = new Member();
        ger.setId(2L);
        ger.setRole("gerente");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getMemberEntity(2L)).thenReturn(ger);

        assertThrows(IllegalArgumentException.class, () -> projectService.addMemberToProject(1L, 2L));
    }
}
