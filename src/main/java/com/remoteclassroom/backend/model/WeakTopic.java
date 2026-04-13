package com.remoteclassroom.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "weak_topic")
public class WeakTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id")
    private QuizAttempt quizAttempt;

    private String topic;
    private int count;

    // ✅ GETTERS & SETTERS

    public Long getId() { return id; }

    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}