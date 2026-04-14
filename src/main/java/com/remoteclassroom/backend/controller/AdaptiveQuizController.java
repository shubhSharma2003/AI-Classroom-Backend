package com.remoteclassroom.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.repository.VideoRepository;
import com.remoteclassroom.backend.service.AdaptiveQuizService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/adaptive")
public class AdaptiveQuizController {

    @Autowired
    private AdaptiveQuizService adaptiveService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/quiz/{videoId}")
    public ResponseEntity<?> generateQuiz(
            @PathVariable Long videoId,
            Authentication authentication) {

        try {
        
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            Quiz quiz = adaptiveService.generateAdaptiveQuiz(user.getId(), video);

            List<Map<String, Object>> questions = mapper.readValue(
                    quiz.getQuestionsJson(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            // SECURE: Strip the correct answers so students can't cheat via DevTools
            questions.forEach(q -> q.remove("correctAnswer"));

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