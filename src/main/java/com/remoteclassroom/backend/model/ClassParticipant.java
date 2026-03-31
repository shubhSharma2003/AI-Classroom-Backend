package com.remoteclassroom.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_participants")
public class ClassParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long classId;

    private String student;

    private LocalDateTime joinedAt;

    public ClassParticipant() {}

    public Long getId() { return id; }

    public Long getClassId() { return classId; }

    public String getStudent() { return student; }

    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setId(Long id) { this.id = id; }

    public void setClassId(Long classId) { this.classId = classId; }

    public void setStudent(String student) { this.student = student; }

    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}