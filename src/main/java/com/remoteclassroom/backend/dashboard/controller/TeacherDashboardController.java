package com.remoteclassroom.backend.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.dashboard.dto.response.TeacherDashboardResponse;
import com.remoteclassroom.backend.dashboard.service.TeacherDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService service;

    @GetMapping
    public ResponseEntity<TeacherDashboardResponse> getDashboard(Authentication auth) {

        String email = auth.getName();

        return ResponseEntity.ok(service.getDashboard(email));
    }
}