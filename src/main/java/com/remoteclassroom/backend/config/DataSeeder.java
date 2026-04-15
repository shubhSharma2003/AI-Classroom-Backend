package com.remoteclassroom.backend.config;

import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {

            // ============================================
            // 👨‍🏫 Seed default TEACHER account
            // ============================================
            if (!userRepository.existsByEmail("teacher@classroom.com")) {
                User teacher = new User();
                teacher.setName("Demo Teacher");
                teacher.setEmail("teacher@classroom.com");
                teacher.setPassword(passwordEncoder.encode("Teacher@123"));
                teacher.setRole("TEACHER");
                userRepository.save(teacher);
                System.out.println("✅ [DataSeeder] Default TEACHER account created: teacher@classroom.com");
            }

            // ============================================
            // 👨‍🎓 Seed default STUDENT account
            // ============================================
            if (!userRepository.existsByEmail("student@classroom.com")) {
                User student = new User();
                student.setName("Demo Student");
                student.setEmail("student@classroom.com");
                student.setPassword(passwordEncoder.encode("Student@123"));
                student.setRole("STUDENT");
                userRepository.save(student);
                System.out.println("✅ [DataSeeder] Default STUDENT account created: student@classroom.com");
            }

        };
    }
}
