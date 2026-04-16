package com.remoteclassroom.backend.service;

import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.dto.RegisterRequest;
import com.remoteclassroom.backend.dto.LoginRequest;
import com.remoteclassroom.backend.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // REGISTER
    public User register(RegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().toUpperCase()); // Store plain (STUDENT/TEACHER)

        return userRepository.save(user);
    }

    // LOGIN
    public Map<String, Object> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() == null || request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String role = user.getRole() != null ? user.getRole() : "STUDENT";

        String token = JwtUtil.generateToken(user.getEmail(), role);

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("role", role);
        return response;
    }
}