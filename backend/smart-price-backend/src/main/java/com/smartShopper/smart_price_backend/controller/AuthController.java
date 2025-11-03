package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.user.LoginRequest;
import com.smartShopper.smart_price_backend.dto.user.RegisterRequest;
import com.smartShopper.smart_price_backend.dto.user.UserResponse;
import com.smartShopper.smart_price_backend.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            UserResponse userResponse = authService.register(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful! Check your email for verification code.");
            response.put("data", userResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            UserResponse userResponse = authService.login(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("data", userResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}