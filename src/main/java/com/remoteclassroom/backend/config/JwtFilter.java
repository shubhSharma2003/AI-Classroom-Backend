package com.remoteclassroom.backend.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🔥 JWT FILTER HIT");
        System.out.println("➡️ PATH: " + request.getRequestURI());

        String header = request.getHeader("Authorization");

        System.out.println("➡️ HEADER: " + header);

        // ❌ No token → skip
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("❌ No Bearer token");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = JwtUtil.extractEmail(token);
            String role = JwtUtil.extractRole(token);

            System.out.println("✅ EMAIL: " + email);
            System.out.println("✅ ROLE: " + role);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // ✅ IMPORTANT: ROLE must match Spring format
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(authority)
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("🎉 AUTH SET SUCCESS");
            }

        } catch (Exception e) {
            System.out.println("❌ JWT ERROR: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}