package com.remoteclassroom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.ClassParticipant;

public interface ClassParticipantRepository extends JpaRepository<ClassParticipant, Long> {

    List<ClassParticipant> findByClassId(Long classId);

    boolean existsByClassIdAndStudent(Long classId, String student);

    long countByClassId(Long classId);
}