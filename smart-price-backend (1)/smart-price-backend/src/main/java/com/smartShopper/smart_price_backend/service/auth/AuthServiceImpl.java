package com.smartShopper.smart_price_backend.service.auth;

import com.smartShopper.smart_price_backend.dto.user.LoginRequest;
import com.smartShopper.smart_price_backend.dto.user.RegisterRequest;
import com.smartShopper.smart_price_backend.dto.user.UserResponse;
import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        // Trim input to remove leading/trailing spaces
        String email = request.getEmail().trim();
        String name = request.getName().trim();
        String password = request.getPassword();

        // Check if email already exists
        if (userService.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create new User entity
        User newUser = new User(name, email, password);

        // Save user
        User saved = userService.create(newUser);

        // Return response
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Override
    public UserResponse login(LoginRequest request) {
        String email = request.getEmail().trim();
        String password = request.getPassword();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
