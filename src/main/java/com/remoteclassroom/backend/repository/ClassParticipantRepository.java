package com.remoteclassroom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.ClassParticipant;

public interface ClassParticipantRepository extends JpaRepository<ClassParticipant, Long> {

    boolean existsByLiveClassIdAndStudentEmail(Long classId, String email);

    long countByLiveClassId(Long classId);

    Optional<ClassParticipant> findByLiveClassIdAndStudentEmail(Long classId, String email);

    List<ClassParticipant> findByLiveClassId(Long classId);
}