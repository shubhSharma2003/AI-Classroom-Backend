package com.remoteclassroom.backend.dashboard.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentQuizResponse {
    private Long quizId;
    private double score;
    private LocalDateTime date;
}