package com.remoteclassroom.backend.dto;

import java.util.List;

public class QuizAttemptResponse {

    private int score;
    private int totalQuestions;
    private double percentage;
    private List<String> weakTopics;

    public QuizAttemptResponse(int score, int totalQuestions, List<String> weakTopics) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.weakTopics = weakTopics;
        this.percentage = (totalQuestions == 0) ? 0 : (score * 100.0) / totalQuestions;
    }

    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getPercentage() { return percentage; }
    public List<String> getWeakTopics() { return weakTopics; }
}