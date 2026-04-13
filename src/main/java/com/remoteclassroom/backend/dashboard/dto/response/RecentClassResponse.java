package com.remoteclassroom.backend.dashboard.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentClassResponse {
    private Long classId;
    private LocalDateTime joinTime;
    private Long duration;
}