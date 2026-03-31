package com.remoteclassroom.backend.controller;

import com.remoteclassroom.backend.dto.DoubtRequest;
import com.remoteclassroom.backend.service.AIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/doubt")
    public String solveDoubt(@RequestBody DoubtRequest request) {
        return aiService.getAnswer(
                request.getQuestion(),
                request.getLanguage()
        );
    }
}