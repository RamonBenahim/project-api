package com.challenge.project_api.dto;

import com.challenge.project_api.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate expectedEndDate;
    private LocalDate realEndDate;

    @NotNull(message = "Total budget is required")
    private BigDecimal totalBudget;

    private String description;

    @NotNull(message = "Manager ID is required")
    private Long managerId;

    @NotNull(message = "Status is required")
    private ProjectStatus status;
}
