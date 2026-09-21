package com.finbank.gateway;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtGatewayFilterTest {
    @Test
    void shouldCreateFilterWithDevelopmentSecret() {
        assertNotNull(new JwtGatewayFilter("finbank-development-secret-key-change-in-production-256-bits"));
    }
}