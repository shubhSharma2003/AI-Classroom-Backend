package com.remoteclassroom.backend.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.dashboard.dto.response.StudentDashboardResponse;
import com.remoteclassroom.backend.dashboard.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<StudentDashboardResponse> getDashboard(
            @AuthenticationPrincipal String Email) {

        return ResponseEntity.ok(
                dashboardService.getDashboard(Email)
        );
    }
}