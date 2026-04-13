package com.remoteclassroom.backend.dashboard.service;

import com.remoteclassroom.backend.dashboard.dto.response.StudentDashboardResponse;

public interface StudentDashboardService {
    StudentDashboardResponse getDashboard(String userId);
}