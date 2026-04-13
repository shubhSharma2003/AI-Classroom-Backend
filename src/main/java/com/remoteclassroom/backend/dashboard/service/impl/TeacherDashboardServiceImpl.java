package com.remoteclassroom.backend.dashboard.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.dashboard.dto.response.TeacherDashboardResponse;
import com.remoteclassroom.backend.dashboard.dto.response.TeacherRecentClassResponse;
import com.remoteclassroom.backend.dashboard.dto.response.TeacherRecentQuizStatsResponse;
import com.remoteclassroom.backend.dashboard.repository.TeacherDashboardRepository;
import com.remoteclassroom.backend.dashboard.service.TeacherDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private final TeacherDashboardRepository repo;

    @Override
    public TeacherDashboardResponse getDashboard(String email) {

        TeacherDashboardResponse res = new TeacherDashboardResponse();

        res.setTotalClassesCreated(
                Optional.ofNullable(repo.totalClasses(email)).orElse(0L)
        );

        res.setTotalStudents(
                Optional.ofNullable(repo.totalStudents(email)).orElse(0L)
        );

        res.setAverageAttendancePerClass(
                Optional.ofNullable(repo.avgAttendance(email)).orElse(0.0)
        );

        res.setAverageStudentScore(
                Optional.ofNullable(repo.avgScore(email)).orElse(0.0)
        );

        // ✅ Weak Topics
        List<String> topics = repo.topWeakTopics(email, PageRequest.of(0, 5))
                .stream()
                .map(obj -> (String) obj[0])
                .toList();

        res.setMostCommonWeakTopics(topics);

        // ✅ Recent Classes (SAFE CAST)
        List<TeacherRecentClassResponse> classes =
                repo.recentClasses(email, PageRequest.of(0, 5))
                        .stream()
                        .map(obj -> new TeacherRecentClassResponse(
                                (String) obj[0],
                                (java.time.LocalDateTime) obj[1],
                                obj[2] != null ? ((Number) obj[2]).longValue() : 0L
                        ))
                        .toList();

        res.setRecentClasses(classes);

        // ✅ Quiz Stats (SAFE CAST)
        List<TeacherRecentQuizStatsResponse> quizzes =
                repo.recentQuizStats(email, PageRequest.of(0, 5))
                        .stream()
                        .map(obj -> new TeacherRecentQuizStatsResponse(
                                (String) obj[0],
                                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0,
                                obj[2] != null ? ((Number) obj[2]).longValue() : 0L
                        ))
                        .toList();

        res.setRecentQuizStats(quizzes);

        return res;
    }
}