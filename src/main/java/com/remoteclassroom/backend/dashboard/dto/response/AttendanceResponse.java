package com.remoteclassroom.backend.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {
    private int totalClasses;
    private int attendedClasses;
    private double attendancePercentage;
    private int totalDurationMinutes;
}