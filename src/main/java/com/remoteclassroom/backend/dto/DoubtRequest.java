package com.remoteclassroom.backend.dto;

public class DoubtRequest {

    private String question;
    private String language;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}