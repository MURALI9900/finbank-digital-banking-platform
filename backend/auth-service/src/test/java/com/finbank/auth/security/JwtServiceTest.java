package com.finbank.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService service = new JwtService(
            "finbank-development-secret-key-change-in-production-256-bits", 3600000);

    @Test
    void shouldGenerateAndParseToken() {
        String token = service.generateToken("murali", "CUSTOMER");
        var claims = service.parseToken(token);
        assertEquals("murali", claims.getSubject());
        assertEquals("CUSTOMER", claims.get("role", String.class));
    }
}