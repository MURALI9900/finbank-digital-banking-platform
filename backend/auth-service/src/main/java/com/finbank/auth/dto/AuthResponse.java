package com.finbank.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, String username, String role, String customerNumber) {}
