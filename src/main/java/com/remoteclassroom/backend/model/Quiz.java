package com.remoteclassroom.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    private String difficulty;

    @Lob
    private String questionsJson;

    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public Long getVideoId() { return videoId; }
    public String getDifficulty() { return difficulty; }
    public String getQuestionsJson() { return questionsJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setQuestionsJson(String questionsJson) { this.questionsJson = questionsJson; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}