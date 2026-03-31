package com.remoteclassroom.backend.dto;

public class QuizAttemptResponse {

    private int score;
    private String message;

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}