package com.remoteclassroom.backend.dashboard.service;

import com.remoteclassroom.backend.dashboard.dto.response.TeacherDashboardResponse;

public interface TeacherDashboardService {
    TeacherDashboardResponse getDashboard(String email);
}