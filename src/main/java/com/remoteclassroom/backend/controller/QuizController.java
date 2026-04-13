package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/video/{videoId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long videoId, Authentication auth) {

        String email = auth.getName();

        Quiz quiz = quizService.getOrGenerateQuiz(videoId, email);

        return ResponseEntity.ok(quiz);
    }
}