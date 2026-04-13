package com.remoteclassroom.backend.dashboard.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherRecentClassResponse {
    private String title;
    private LocalDateTime startTime;
    private Long totalParticipants;
}