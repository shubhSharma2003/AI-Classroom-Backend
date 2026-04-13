package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.remoteclassroom.backend.service.AuthService;
import com.remoteclassroom.backend.dto.RegisterRequest;
import com.remoteclassroom.backend.dto.LoginRequest;
import com.remoteclassroom.backend.model.User;

import jakarta.validation.Valid;

import java.util.Map; 

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "data", Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                ),
                "message", "User registered successfully"
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}