package com.remoteclassroom.backend.dashboard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.remoteclassroom.backend.dashboard.dto.response.RecentQuizResponse;
import com.remoteclassroom.backend.model.QuizAttempt;

public interface DashboardQuizRepository extends JpaRepository<QuizAttempt, Long> {

    // ===== PERFORMANCE =====
    @Query("""
        SELECT COUNT(q), AVG(q.score)
        FROM QuizAttempt q
        WHERE q.student.email = :userEmail
    """)
    List<Object[]> getPerformanceStats(@Param("userEmail") String userEmail); // ✅ FIXED

    // ===== LAST SCORE =====
    @Query("""
        SELECT q.score
        FROM QuizAttempt q
        WHERE q.student.email = :userEmail
        ORDER BY q.attemptedAt DESC
    """)
    List<Integer> getLastScore(@Param("userEmail") String userEmail, Pageable pageable);

    // ===== RECENT QUIZZES =====
    @Query("""
        SELECT new com.remoteclassroom.backend.dashboard.dto.response.RecentQuizResponse(
            q.id,
            q.score,
            q.attemptedAt
        )
        FROM QuizAttempt q
        WHERE q.student.email = :userEmail
        ORDER BY q.attemptedAt DESC
    """)
    List<RecentQuizResponse> getRecentQuizzes(
            @Param("userEmail") String userEmail,
            Pageable pageable
    );
}