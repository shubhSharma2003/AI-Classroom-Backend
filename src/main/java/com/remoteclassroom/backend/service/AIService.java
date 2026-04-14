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

    // =========================
    // 📘 EXPLANATION API (DOUBTS)
    // =========================
    public String getAnswer(String question, String language) {

        String prompt = """
You are an expert, encouraging AI teacher.

A student has asked the following doubt. Please explain it clearly in %s.

Guidelines for your response:
1. Structure: Use markdown format. Use bolding to highlight keywords. Draw clear bullet points to make it extremely readable.
2. Clarity: Avoid overly complex jargon. Keep sentences concise.
3. Examples: Always provide exactly one simple real-world or code example to solidify the concept.
4. Tone: Be friendly, professional, and directly address the student.

Question from Student:
%s
""".formatted(language, question);

        try {
            // Because this is natural text, we do NOT run cleanJson! 
            // We want to preserve \n so paragraphs render correctly on the UI.
            return callGeminiCore(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "I'm sorry, I am experiencing a temporary technical glitch connecting to the central mainframe. Please try asking your doubt again in a moment!";
        }
    }

    // =========================
    // 🧠 QUIZ GENERATION (UPGRADED)
    // =========================
    public String generateQuiz(String transcript, String difficulty, String weakTopic) {

        String prompt = """
You are an expert teacher.

Generate EXACTLY 10 MCQs in English.

Difficulty: %s
%s

Rules:
- Questions must be clear and concept-based
- Avoid random questions

STRICT JSON FORMAT:
[
  {
    "question": "string",
    "options": ["A","B","C","D"],
    "correctAnswer": "exact match",
    "topic": "short topic"
  }
]

Return ONLY JSON.

Lecture:
%s
""".formatted(
                difficulty,
                (weakTopic != null && !weakTopic.isBlank()
                        ? "Focus MORE on weak topic: " + weakTopic
                        : ""),
                transcript
        );

        try {
            // We run cleanJson here specifically to protect the JSON parser 
            // without destroying the Doubt API logic above.
            String rawResponse = callGeminiCore(prompt);
            return cleanJson(rawResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return """
[
  {
    "question": "An error occurred generating an adaptive quiz. This is a fallback dummy question.",
    "options": ["A", "B", "C", "D"],
    "correctAnswer": "A",
    "topic": "Fallback"
  }
]
""";
        }
    }

    // =========================
    // 🔥 CORE GEMINI API CALL
    // =========================
    private String callGeminiCore(String prompt) throws Exception {

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
    }

    // =========================
    // 🔥 EXTRACT TEXT
    // =========================
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

        return parts.get(0).get("text").toString();
    }

    // =========================
    // 🧹 CLEAN JSON (ONLY FOR QUIZZES)
    // =========================
    private String cleanJson(String text) {

        return text
                .replace("```json", "")
                .replace("```", "")
                .replace("\n", "")
                .replace("\r", "")
                .trim();
    }
}