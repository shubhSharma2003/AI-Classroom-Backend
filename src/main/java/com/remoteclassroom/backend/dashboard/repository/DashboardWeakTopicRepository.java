package com.remoteclassroom.backend.dashboard.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.remoteclassroom.backend.model.WeakTopic;

public interface DashboardWeakTopicRepository extends JpaRepository<WeakTopic, Long> {

    @Query("""
    SELECT wt.topic, COUNT(wt)
    FROM WeakTopic wt
    WHERE wt.quizAttempt.student.email = :email
    GROUP BY wt.topic
    ORDER BY COUNT(wt) DESC
""")
List<Object[]> weakTopics(@Param("email") String email);
}