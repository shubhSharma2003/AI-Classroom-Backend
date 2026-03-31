package com.remoteclassroom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.remoteclassroom.backend.model.WeakTopic;

import jakarta.transaction.Transactional;

public interface WeakTopicRepository extends JpaRepository<WeakTopic, Long> {

    Optional<WeakTopic> findByUserIdAndTopic(Long userId, String topic);

    List<WeakTopic> findByUserIdOrderByCountDesc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE WeakTopic w SET w.count = w.count + 1 WHERE w.userId = :userId AND w.topic = :topic")
    int incrementCount(Long userId, String topic);
}