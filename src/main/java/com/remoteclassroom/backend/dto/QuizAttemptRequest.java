package com.remoteclassroom.backend.dto;

import java.util.Map;

public class QuizAttemptRequest {

    private Long quizId;

    // index → answer (0,1,2...)
    private Map<Integer, String> answers;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
    }
}