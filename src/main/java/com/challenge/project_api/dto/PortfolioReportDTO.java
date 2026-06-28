package com.challenge.project_api.dto;

import com.challenge.project_api.domain.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class PortfolioReportDTO {
    private Map<ProjectStatus, Long> projectsByStatus;
    private Map<ProjectStatus, BigDecimal> totalBudgetByStatus;
    private Double averageDurationClosedProjectsDays;
    private Long totalUniqueMembersAllocated;
}
