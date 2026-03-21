package com.artwell.web;

import com.artwell.api.dto.DashboardStatistics;
import com.artwell.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public DashboardStatistics statistics() {
        return statisticsService.getStatistics();
    }
}
