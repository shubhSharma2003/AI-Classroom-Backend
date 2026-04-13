package com.remoteclassroom.backend.dashboard.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class TeacherDashboardResponse {

    private long totalClassesCreated;
    private long totalStudents;
    private double averageAttendancePerClass;
    private double averageStudentScore;

    private List<String> mostCommonWeakTopics;

    private List<TeacherRecentClassResponse> recentClasses;
    private List<TeacherRecentQuizStatsResponse> recentQuizStats;
}