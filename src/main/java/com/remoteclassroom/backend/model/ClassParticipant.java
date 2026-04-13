package com.remoteclassroom.backend.model;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_participants")
public class ClassParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private LiveClass liveClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Long durationSeconds;

    public void calculateDuration() {
        if (joinedAt != null && leftAt != null) {
            this.durationSeconds = Duration.between(joinedAt, leftAt).getSeconds();
        }
    }

    // ✅ GETTERS & SETTERS

    public Long getId() { return id; }

    public LiveClass getLiveClass() { return liveClass; }
    public void setLiveClass(LiveClass liveClass) { this.liveClass = liveClass; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public LocalDateTime getLeftAt() { return leftAt; }
    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }

    public Long getDurationSeconds() { return durationSeconds; }
}