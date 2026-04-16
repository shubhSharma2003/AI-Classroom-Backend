package com.remoteclassroom.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =============================================
    // 🔐 AUTH ERRORS — Return 401 (not 500)
    // =============================================
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuthException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // =============================================
    // 🚫 NOT AUTHENTICATED — Return 401
    // =============================================
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", "You must be logged in to access this resource.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // =============================================
    // 🚫 ACCESS DENIED — Return 403 (not 500!)
    // =============================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", "Access denied. You do not have permission to perform this action.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // =============================================
    // ⚠️ RUNTIME ERRORS — e.g. "User not found", "Invalid password"
    // =============================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        // Auth-related runtime errors → 401
        if (message != null && (message.contains("User not found") || message.contains("Invalid password"))) {
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        // Not found patterns → 404
        if (message != null && message.toLowerCase().contains("not found")) {
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("error", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        // All other runtime exceptions → 400 Bad Request
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", message != null ? message : "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // =============================================
    // ✅ VALIDATION ERRORS — Return 400 with JSON
    // =============================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);

        if ("WEAK_PASSWORD".equals(errorMessage)) {
            body.put("error", "Password too weak. Try: " + generateStrongPassword());
        } else {
            body.put("error", "Invalid input. Please check your details.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // =============================================
    // 🔥 LAST RESORT — Catch any unexpected Exception
    // =============================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", "An internal error occurred. Please try again later.");
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String generateStrongPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%!";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}