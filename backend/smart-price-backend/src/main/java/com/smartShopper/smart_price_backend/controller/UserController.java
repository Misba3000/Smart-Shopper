package com.smartShopper.smart_price_backend.controller;

import com.smartShopper.smart_price_backend.dto.common.ApiResponse;
import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService s) { this.userService = s; }

    // ✅ Create user
    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody User u) {
        User created = userService.create(u);
        return ResponseEntity.ok(ApiResponse.ok("User created", created));
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> all() {
        return ResponseEntity.ok(ApiResponse.ok("Users", userService.findAll()));
    }
}
