package com.smartShopper.smart_price_backend.service.auth;

import com.smartShopper.smart_price_backend.dto.user.LoginRequest;
import com.smartShopper.smart_price_backend.dto.user.RegisterRequest;
import com.smartShopper.smart_price_backend.dto.user.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request);
}
