package com.challenge.project_api.service;

import com.challenge.project_api.domain.entity.Project;
import com.challenge.project_api.domain.enums.ProjectStatus;
import com.challenge.project_api.dto.PortfolioReportDTO;
import com.challenge.project_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectRepository projectRepository;

    public PortfolioReportDTO generateReport() {
        List<Object[]> countByStatus = projectRepository.countProjectsByStatus();
        Map<ProjectStatus, Long> projectsByStatus = new HashMap<>();
        for (Object[] row : countByStatus) {
            projectsByStatus.put((ProjectStatus) row[0], (Long) row[1]);
        }

        List<Object[]> sumByStatus = projectRepository.sumBudgetByStatus();
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus = new HashMap<>();
        for (Object[] row : sumByStatus) {
            totalBudgetByStatus.put((ProjectStatus) row[0], (BigDecimal) row[1]);
        }

        List<Project> closedProjects = projectRepository.findAllByStatus(ProjectStatus.ENCERRADO);
        double averageDuration = 0.0;
        if (!closedProjects.isEmpty()) {
            long totalDays = 0;
            for (Project p : closedProjects) {
                if (p.getStartDate() != null && p.getRealEndDate() != null) {
                    totalDays += ChronoUnit.DAYS.between(p.getStartDate(), p.getRealEndDate());
                }
            }
            averageDuration = (double) totalDays / closedProjects.size();
        }

        long uniqueMembers = projectRepository.countDistinctMembersInProjects();

        return PortfolioReportDTO.builder()
                .projectsByStatus(projectsByStatus)
                .totalBudgetByStatus(totalBudgetByStatus)
                .averageDurationClosedProjectsDays(averageDuration)
                .totalUniqueMembersAllocated(uniqueMembers)
                .build();
    }
}
