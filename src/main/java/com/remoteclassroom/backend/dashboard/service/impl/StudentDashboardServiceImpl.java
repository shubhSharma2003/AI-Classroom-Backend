package com.remoteclassroom.backend.dashboard.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.dashboard.dto.response.AttendanceResponse;
import com.remoteclassroom.backend.dashboard.dto.response.PerformanceResponse;
import com.remoteclassroom.backend.dashboard.dto.response.RecentClassResponse;
import com.remoteclassroom.backend.dashboard.dto.response.RecentQuizResponse;
import com.remoteclassroom.backend.dashboard.dto.response.StudentDashboardResponse;
import com.remoteclassroom.backend.dashboard.repository.DashboardParticipantRepository;
import com.remoteclassroom.backend.dashboard.repository.DashboardQuizRepository;
import com.remoteclassroom.backend.dashboard.repository.DashboardWeakTopicRepository;
import com.remoteclassroom.backend.dashboard.service.StudentDashboardService;
import com.remoteclassroom.backend.service.RecommendationService;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final DashboardQuizRepository quizRepository;
    private final DashboardParticipantRepository attendanceRepository;
    private final DashboardWeakTopicRepository weakTopicRepository;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @Override
    public StudentDashboardResponse getDashboard(String userEmail) {

        // ================= PERFORMANCE =================
        List<Object[]> perfList = quizRepository.getPerformanceStats(userEmail);

        Object[] perf = (perfList == null || perfList.isEmpty())
                ? new Object[]{0L, 0.0}
                : perfList.get(0);

        int totalQuizzes = perf[0] != null ? ((Number) perf[0]).intValue() : 0;
        double avgScore = perf[1] != null ? ((Number) perf[1]).doubleValue() : 0.0;

        Double lastScore = quizRepository
                .getLastScore(userEmail, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(Integer::doubleValue)
                .orElse(0.0);

        PerformanceResponse performance = new PerformanceResponse(
                totalQuizzes,
                avgScore,
                lastScore
        );

        // ================= WEAK TOPICS =================
        List<Object[]> weakTopicData = weakTopicRepository.weakTopics(userEmail);

        List<String> weakTopics = weakTopicData == null ? List.of() :
                weakTopicData.stream()
                        .map(obj -> (String) obj[0])
                        .toList();

        // ================= ATTENDANCE =================
        List<Object[]> attList = attendanceRepository.getAttendanceStats(userEmail);

        Object[] att = (attList == null || attList.isEmpty())
                ? new Object[]{0L, 0L, 0L}
                : attList.get(0);

        int totalClasses = att[0] != null ? ((Number) att[0]).intValue() : 0;
        int attendedClasses = att[1] != null ? ((Number) att[1]).intValue() : 0;
        int totalDurationSeconds = att[2] != null ? ((Number) att[2]).intValue() : 0;

        int totalDurationMinutes = totalDurationSeconds / 60;

        double percentage = totalClasses == 0 ? 0 :
                (attendedClasses * 100.0) / totalClasses;

        AttendanceResponse attendance = new AttendanceResponse(
                totalClasses,
                attendedClasses,
                percentage,
                totalDurationMinutes
        );

        // ================= RECENT =================
        List<RecentQuizResponse> recentQuizzes =
                quizRepository.getRecentQuizzes(userEmail, PageRequest.of(0, 5));

        List<RecentClassResponse> recentClasses =
                attendanceRepository.getRecentClasses(userEmail, PageRequest.of(0, 5));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return StudentDashboardResponse.builder()
                .performance(performance)
                .weakTopics(weakTopics)
                .attendance(attendance)
                .recentQuizzes(recentQuizzes)
                .recentClasses(recentClasses)
                .overallTrend(recommendationService.getOverallTrend(user.getId()))
                .recommendedActions(recommendationService.getRecommendations(user.getId()))
                .build();
    }
}