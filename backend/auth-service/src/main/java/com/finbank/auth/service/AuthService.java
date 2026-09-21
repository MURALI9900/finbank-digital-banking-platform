package com.finbank.auth.service;

import com.finbank.auth.dto.AuthResponse;
import com.finbank.auth.dto.LoginRequest;
import com.finbank.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}