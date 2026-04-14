package com.remoteclassroom.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.dashboard.dto.response.ProgressTrendResponse;
import com.remoteclassroom.backend.dashboard.dto.response.RecommendationResponse;
import com.remoteclassroom.backend.model.QuizAttempt;
import com.remoteclassroom.backend.model.StudentTopicMastery;
import com.remoteclassroom.backend.repository.QuizAttemptRepository;
import com.remoteclassroom.backend.repository.StudentTopicMasteryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final StudentTopicMasteryRepository masteryRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public List<RecommendationResponse> getRecommendations(Long studentId) {
        List<StudentTopicMastery> masteries = masteryRepository.findByStudent_IdOrderByMasteryLevelAsc(studentId);
        List<RecommendationResponse> recommendations = new ArrayList<>();

        // Generate recommendations for topics where mastery < 60%
        for (StudentTopicMastery mastery : masteries) {
            if (mastery.getMasteryLevel() < 60.0) {
                recommendations.add(RecommendationResponse.builder()
                        .type("RETAKE_QUIZ")
                        .focus(mastery.getTopicName())
                        .build());
            }
        }
        
        // If mastery is generally high, encourage a challenge
        if (recommendations.isEmpty() && !masteries.isEmpty()) {
            recommendations.add(RecommendationResponse.builder()
                    .type("TAKE_ADVANCED_QUIZ")
                    .focus(masteries.get(masteries.size() - 1).getTopicName()) // focus on their strongest topic for challenge
                    .build());
        }

        return recommendations;
    }

    public List<ProgressTrendResponse> getOverallTrend(Long studentId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByStudent_Id(studentId);
        
        // Return recent trends (last 10)
        return attempts.stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .skip(Math.max(0, attempts.size() - 10))
                .map(attempt -> ProgressTrendResponse.builder()
                        .date(attempt.getQuiz().getCreatedAt() != null ? attempt.getQuiz().getCreatedAt().toString() : "N/A")
                        .score(attempt.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}
