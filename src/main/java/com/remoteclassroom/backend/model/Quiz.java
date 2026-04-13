package com.remoteclassroom.backend.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private String difficulty;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String questionsJson;

    private int totalQuestions;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // GETTERS
    public Long getId() { return id; }
    public String getDifficulty() { return difficulty; }
    public String getQuestionsJson() { return questionsJson; }
    public int getTotalQuestions() { return totalQuestions; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTERS
    public void setVideo(Video video) { this.video = video; }
    public void setTeacher(User teacher) { this.teacher = teacher; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setQuestionsJson(String questionsJson) { this.questionsJson = questionsJson; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    // ✅ THIS WAS MISSING
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}