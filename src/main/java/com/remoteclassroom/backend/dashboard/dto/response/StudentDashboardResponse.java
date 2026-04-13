package com.remoteclassroom.backend.dashboard.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDashboardResponse {
    private PerformanceResponse performance;
    private List<String> weakTopics;
    private AttendanceResponse attendance;
    private List<RecentQuizResponse> recentQuizzes;
    private List<RecentClassResponse> recentClasses;
}