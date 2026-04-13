package com.remoteclassroom.backend.dashboard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.remoteclassroom.backend.dashboard.dto.response.RecentClassResponse;
import com.remoteclassroom.backend.model.ClassParticipant;

public interface DashboardParticipantRepository extends JpaRepository<ClassParticipant, Long> {

    // ===== ATTENDANCE =====
    @Query("""
        SELECT COUNT(p),
               SUM(CASE WHEN p.durationSeconds > 600 THEN 1 ELSE 0 END),
               COALESCE(SUM(p.durationSeconds), 0)
        FROM ClassParticipant p
        WHERE p.student.email = :userEmail
    """)
    List<Object[]> getAttendanceStats(@Param("userEmail") String userEmail); // ✅ FIXED

    // ===== RECENT CLASSES =====
    @Query("""
        SELECT new com.remoteclassroom.backend.dashboard.dto.response.RecentClassResponse(
            p.liveClass.id,
            p.joinedAt,
            p.durationSeconds
        )
        FROM ClassParticipant p
        WHERE p.student.email = :userEmail
        ORDER BY p.joinedAt DESC
    """)
    List<RecentClassResponse> getRecentClasses(
            @Param("userEmail") String userEmail,
            Pageable pageable
    );
}