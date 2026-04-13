package com.remoteclassroom.backend.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

@Embeddable
public class Question {

    private String question;

    private String correctAnswer;

    private String topic;

    @ElementCollection
    private List<String> options;

    // GETTERS
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getTopic() { return topic; }
    public List<String> getOptions() { return options; }

    // SETTERS
    public void setQuestion(String question) { this.question = question; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setOptions(List<String> options) { this.options = options; }
}