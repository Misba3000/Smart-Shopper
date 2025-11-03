package com.smartShopper.smart_price_backend.service.auth;

import com.smartShopper.smart_price_backend.dto.user.LoginRequest;
import com.smartShopper.smart_price_backend.dto.user.RegisterRequest;
import com.smartShopper.smart_price_backend.dto.user.UserResponse;
import com.smartShopper.smart_price_backend.entity.User;
import com.smartShopper.smart_price_backend.service.email.EmailService;
import com.smartShopper.smart_price_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final EmailService emailService;

    private static final String FIXED_VERIFICATION_CODE = "6745";

    @Autowired
    public AuthServiceImpl(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String name = request.getName().trim();
        String password = request.getPassword();
        User.Role role = request.getRole() != null ? request.getRole() : User.Role.USER;

        // Check if email already exists
        if (userService.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Validate password length
        if (password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // Create user object
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);

        // Save user
        User saved = userService.create(newUser);
        System.out.println("✅ User created: " + saved.getEmail());

        // Send verification email asynchronously (only for regular users)
        if (saved.getRole() == User.Role.USER) {
            try {
                String subject = "🎉 Welcome to SmartShopper - Verification Code";
                String message = String.format("""
                        Hi %s,
                        
                        Welcome to SmartShopper! 💸
                        
                        Your verification code is: %s
                        
                        Please enter this code when logging in to verify your account.
                        
                        Thank you for joining SmartShopper!
                        
                        Best regards,
                        The SmartShopper Team
                        """, name, FIXED_VERIFICATION_CODE);

                emailService.sendEmail(email, subject, message);
                System.out.println("📧 Verification email queued for: " + email);
            } catch (Exception e) {
                System.err.println("❌ Email send failed but user created: " + e.getMessage());
            }
        } else {
            System.out.println("🔑 Admin account created - no verification code sent");
        }

        return new UserResponse(saved);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();
        String verificationCode = request.getVerificationCode();

        // Validate user
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Password check
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }

        // ✅ ADMIN BYPASS: Skip verification code for admin users
        if (user.getRole() == User.Role.ADMIN) {
            System.out.println("🔑 Admin logged in (no OTP required): " + email);
            return new UserResponse(user);
        }

        // ✅ Regular USER: Require verification code
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            throw new RuntimeException("Verification code is required");
        }

        if (!verificationCode.trim().equals(FIXED_VERIFICATION_CODE)) {
            throw new RuntimeException("Invalid verification code");
        }

        // Role check (if provided)
        if (request.getRole() != null && !user.getRole().equals(request.getRole())) {
            throw new RuntimeException("Invalid role");
        }

        System.out.println("✅ User logged in: " + email);
        return new UserResponse(user);
    }
}