package com.smartShopper.smart_price_backend.dto.user;

import com.smartShopper.smart_price_backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private User.Role role;

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
