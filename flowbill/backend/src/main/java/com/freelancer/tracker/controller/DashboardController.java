package com.freelancer.tracker.controller;

import com.freelancer.tracker.dto.DashboardDTO;
import com.freelancer.tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}