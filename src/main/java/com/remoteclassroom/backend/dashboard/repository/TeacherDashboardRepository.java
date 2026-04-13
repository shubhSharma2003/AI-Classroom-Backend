package com.remoteclassroom.backend.dashboard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.remoteclassroom.backend.model.LiveClass;

public interface TeacherDashboardRepository extends JpaRepository<LiveClass, Long> {

    // 1. Total Classes
    @Query("""
        SELECT COUNT(lc)
        FROM LiveClass lc
        WHERE lc.teacher.email = :email
    """)
    Long totalClasses(@Param("email") String email);

    // 2. Total Students
    @Query("""
        SELECT COUNT(DISTINCT cp.student.id)
        FROM ClassParticipant cp
        WHERE cp.liveClass.teacher.email = :email
    """)
    Long totalStudents(@Param("email") String email);

    // 3. Avg Attendance
    @Query("""
        SELECT AVG(size(lc.participants))
        FROM LiveClass lc
        WHERE lc.teacher.email = :email
    """)
    Double avgAttendance(@Param("email") String email);

    // 4. Avg Score
    @Query("""
        SELECT AVG(qa.score)
        FROM QuizAttempt qa
        WHERE qa.quiz.teacher.email = :email
    """)
    Double avgScore(@Param("email") String email);

    // 5. Weak Topics
    @Query("""
        SELECT wt.topic, COUNT(wt)
        FROM WeakTopic wt
        WHERE wt.quizAttempt.quiz.teacher.email = :email
        GROUP BY wt.topic
        ORDER BY COUNT(wt) DESC
    """)
    List<Object[]> topWeakTopics(@Param("email") String email, Pageable pageable);

    // 6. Recent Classes
    @Query("""
        SELECT lc.title, lc.startTime, COUNT(cp)
        FROM LiveClass lc
        LEFT JOIN lc.participants cp
        WHERE lc.teacher.email = :email
        GROUP BY lc.id, lc.title, lc.startTime
        ORDER BY lc.startTime DESC
    """)
    List<Object[]> recentClasses(@Param("email") String email, Pageable pageable);

    // 7. Quiz Stats
    @Query("""
        SELECT q.video.title, AVG(qa.score), COUNT(qa)
        FROM Quiz q
        LEFT JOIN QuizAttempt qa ON qa.quiz.id = q.id
        WHERE q.teacher.email = :email
        GROUP BY q.video.title
        ORDER BY MAX(qa.attemptedAt) DESC
    """)
    List<Object[]> recentQuizStats(@Param("email") String email, Pageable pageable);
}