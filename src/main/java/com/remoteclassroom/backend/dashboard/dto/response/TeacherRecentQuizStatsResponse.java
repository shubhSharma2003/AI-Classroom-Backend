package com.remoteclassroom.backend.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherRecentQuizStatsResponse {
    private String title;
    private double avgScore;
    private Long totalAttempts;
}