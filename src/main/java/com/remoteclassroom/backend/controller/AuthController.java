package com.remoteclassroom.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.remoteclassroom.backend.config.JwtUtil;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@Valid @RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User dbUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            return "Invalid credentials";
        }

        return JwtUtil.generateToken(user.getEmail());
    }
}