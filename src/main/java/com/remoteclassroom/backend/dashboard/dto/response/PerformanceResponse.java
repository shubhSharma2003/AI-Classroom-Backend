package com.remoteclassroom.backend.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceResponse {
    private int totalQuizzes;
    private double averageScore;
    private double lastScore;
}