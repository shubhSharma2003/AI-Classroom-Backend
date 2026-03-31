package com.remoteclassroom.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.service.AdaptiveQuizService;

@RestController
@RequestMapping("/api/adaptive")
public class AdaptiveQuizController {

    @Autowired
    private AdaptiveQuizService adaptiveService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/quiz")
    public ResponseEntity<?> generateQuiz(
            @RequestBody String transcript,
            Authentication authentication) {

        try {
        
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Quiz quiz = adaptiveService.generateAdaptiveQuiz(user.getId(), transcript);

            Object questions = mapper.readValue(
                    quiz.getQuestionsJson(),
                    Object.class
            );

            return ResponseEntity.ok(
                    Map.of(
                            "id", quiz.getId(),
                            "difficulty", quiz.getDifficulty(),
                            "questions", questions
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "error", "Failed to generate adaptive quiz",
                            "message", e.getMessage()
                    )
            );
        }
    }
}