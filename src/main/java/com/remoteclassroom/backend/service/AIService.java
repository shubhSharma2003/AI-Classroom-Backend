package com.remoteclassroom.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();

   
    public String getAnswer(String question, String language) {

        String prompt = "Explain clearly in simple " + language + ":\n" + question;

        return callGemini(prompt);
    }

    
    public String generateQuiz(String transcript, String difficulty, String weakTopic) {

        String prompt = "Generate 5 MCQs from the following lecture.\n" +
                "Difficulty: " + difficulty + "\n" +
                (weakTopic != null && !weakTopic.isEmpty()
                        ? "Focus more on topic: " + weakTopic + "\n"
                        : "") +
                "Each question must include:\n" +
                "- question\n- options\n- correctAnswer\n- topic\n\n" +
                "Return ONLY valid JSON array. Do NOT use markdown, backticks, or explanation.\n\n" +
                "Lecture:\n" + transcript;

        return callGemini(prompt);
    }

   
    private String callGemini(String prompt) {

        try {
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> request = new HashMap<>();
            request.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_URL + apiKey,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return extractText(response);

        } catch (Exception e) {
            e.printStackTrace();

            return "[{" +
                    "\"question\":\"What is Java?\"," +
                    "\"topic\":\"Programming Basics\"," +
                    "\"options\":[\"Lang\",\"DB\",\"OS\",\"Browser\"]," +
                    "\"correctAnswer\":\"Lang\"" +
                    "}]";
        }
    }

   
    private String extractText(ResponseEntity<Map> response) {

        if (response.getBody() == null) return "[]";

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.getBody().get("candidates");

        if (candidates == null || candidates.isEmpty()) return "[]";

        Map<String, Object> first = candidates.get(0);

        Map<String, Object> content =
                (Map<String, Object>) first.get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) return "[]";

        String text = parts.get(0).get("text").toString();

        text = text.replace("```json", "")
                   .replace("```", "")
                   .trim();

        return text;
    }
}