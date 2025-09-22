package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.user.LoginRequest;
import com.smartShopper.smart_price_backend.dto.user.RegisterRequest;
import com.smartShopper.smart_price_backend.dto.user.UserResponse;
import com.smartShopper.smart_price_backend.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        request.setEmail(request.getEmail().trim());
        request.setName(request.getName().trim());
        return authService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
