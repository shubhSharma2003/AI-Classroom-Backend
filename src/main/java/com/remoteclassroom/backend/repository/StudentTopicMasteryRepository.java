package com.remoteclassroom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.remoteclassroom.backend.model.StudentTopicMastery;

@Repository
public interface StudentTopicMasteryRepository extends JpaRepository<StudentTopicMastery, Long> {

    Optional<StudentTopicMastery> findByStudent_IdAndTopicName(Long studentId, String topicName);

    List<StudentTopicMastery> findByStudent_IdOrderByMasteryLevelAsc(Long studentId);
}
