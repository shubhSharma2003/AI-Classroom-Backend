package com.remoteclassroom.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_attempt")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long quizId;

    @Lob
    private String answersJson;

    private int score;

    private LocalDateTime attemptedAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getQuizId() { return quizId; }
    public String getAnswersJson() { return answersJson; }
    public int getScore() { return score; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }
    public void setScore(int score) { this.score = score; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}