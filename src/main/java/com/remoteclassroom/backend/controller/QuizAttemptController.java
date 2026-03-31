package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.dto.QuizAttemptRequest;
import com.remoteclassroom.backend.dto.QuizAttemptResponse;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.service.QuizAttemptService;

@RestController
@RequestMapping("/api/quiz")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService attemptService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(
            @RequestBody QuizAttemptRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        QuizAttemptResponse response = attemptService.submitQuiz(userId, request);

        return ResponseEntity.ok(response);
    }
}