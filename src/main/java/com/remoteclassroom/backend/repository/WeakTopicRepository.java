package com.remoteclassroom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.WeakTopic;

public interface WeakTopicRepository extends JpaRepository<WeakTopic, Long> {

    // ✅ FIXED: find by student + topic
    Optional<WeakTopic> findByQuizAttempt_Student_IdAndTopicIgnoreCase(Long userId, String topic);

    // ✅ FIXED: top weak topics
    List<WeakTopic> findByQuizAttempt_Student_IdOrderByCountDesc(Long userId);
}