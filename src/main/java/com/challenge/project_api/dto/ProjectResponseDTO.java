package com.challenge.project_api.dto;

import com.challenge.project_api.domain.enums.ProjectStatus;
import com.challenge.project_api.domain.enums.RiskLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate realEndDate;
    private BigDecimal totalBudget;
    private String description;
    private MemberDTO manager;
    private ProjectStatus status;
    private RiskLevel riskLevel;
    private Set<MemberDTO> members;
}
