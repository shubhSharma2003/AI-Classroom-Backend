package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.remoteclassroom.backend.dto.QuizAttemptRequest;
import com.remoteclassroom.backend.dto.QuizAttemptResponse;
import com.remoteclassroom.backend.service.QuizAttemptService;

@RestController
@RequestMapping("/api/quiz")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService attemptService;

    @PostMapping("/submit")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(
            @RequestBody QuizAttemptRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(attemptService.submitQuiz(email, request));
    }
}