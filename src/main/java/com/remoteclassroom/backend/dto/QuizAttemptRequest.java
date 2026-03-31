package com.remoteclassroom.backend.dto;

import java.util.List;
import java.util.Map;

public class QuizAttemptRequest {

    private Long quizId;
    private List<Map<String, Object>> answersJson;

    public Long getQuizId() {
        return quizId;
    }

    public List<Map<String, Object>> getAnswersJson() {
        return answersJson;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setAnswersJson(List<Map<String, Object>> answersJson) {
        this.answersJson = answersJson;
    }
}