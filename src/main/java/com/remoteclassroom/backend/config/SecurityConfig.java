package com.remoteclassroom.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth
                // ✅ Auth routes — always public
                .requestMatchers("/api/auth/**").permitAll()

                // ✅ Transcription — public
                .requestMatchers("/api/transcribe/**").permitAll()

                // ✅ Error page — public
                .requestMatchers("/error").permitAll()

                // ✅ Bug Fix #2: Video listing — public (no login required)
                .requestMatchers(HttpMethod.GET, "/api/video/all").permitAll()

                // 🔒 Specific role-protected routes
                .requestMatchers("/api/quiz/submit").hasRole("STUDENT")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .requestMatchers("/api/teacher/**").hasRole("TEACHER")

                // 🔒 Video management — teachers only (upload, save, etc.)
                .requestMatchers("/api/video/**").hasRole("TEACHER")

                // 🔒 Quiz video generation — public, quiz submit — student only
                .requestMatchers("/api/quiz/video/**").permitAll()

                // 🔒 Class management
                .requestMatchers("/api/class/create").hasRole("TEACHER")
                .requestMatchers("/api/class/start/**").hasRole("TEACHER")
                .requestMatchers("/api/class/join/**").hasRole("STUDENT")
                .requestMatchers("/api/class/leave/**").hasRole("STUDENT")
                .requestMatchers("/api/class/**").authenticated()

                // 🔒 User and video download — authenticated
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/video/download/**").authenticated()

                // 🔒 Everything else
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}