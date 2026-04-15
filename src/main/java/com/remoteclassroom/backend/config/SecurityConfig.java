package com.remoteclassroom.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/transcribe/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/video/download/**").authenticated()

                // 🔥 ROLE FIX
                .requestMatchers("/api/quiz/video/**").permitAll()   // generate
                .requestMatchers("/api/quiz/submit").hasRole("STUDENT")

                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .requestMatchers("/api/teacher/**").hasRole("TEACHER")

                .requestMatchers("/api/video/**").hasRole("TEACHER")

                .requestMatchers("/api/class/create").hasRole("TEACHER")
                .requestMatchers("/api/class/start/**").hasRole("TEACHER")

                .requestMatchers("/api/class/join/**").hasRole("STUDENT")
                .requestMatchers("/api/class/leave/**").hasRole("STUDENT")

                .requestMatchers("/api/class/**").authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}