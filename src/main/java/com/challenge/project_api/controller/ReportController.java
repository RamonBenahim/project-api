package com.challenge.project_api.controller;

import com.challenge.project_api.dto.PortfolioReportDTO;
import com.challenge.project_api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/portfolio")
    public PortfolioReportDTO getPortfolioReport() {
        return reportService.generateReport();
    }
}
