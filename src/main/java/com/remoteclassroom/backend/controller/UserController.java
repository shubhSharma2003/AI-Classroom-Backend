package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/user") // ✅ FIXED (singular)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 🔥 PROFILE API
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
}