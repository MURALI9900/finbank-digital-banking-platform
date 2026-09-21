package com.finbank.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {
    private final SecretKey key;

    public JwtGatewayFilter(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/actuator/")) return chain.filter(exchange);
        if (path.startsWith("/api/v1/accounts/internal/")) return reject(exchange, HttpStatus.FORBIDDEN);
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return reject(exchange, HttpStatus.UNAUTHORIZED);
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(header.substring(7)).getPayload();
            String role = claims.get("role", String.class);
            if (!isAllowed(path, role)) return reject(exchange, HttpStatus.FORBIDDEN);
            return chain.filter(exchange);
        } catch (Exception ex) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isAllowed(String path, String role) {
        if (role == null) return false;
        if (path.startsWith("/api/v1/officers/") || path.startsWith("/api/v1/audits/"))
            return Set.of("OFFICER", "ADMIN").contains(role);
        return Set.of("CUSTOMER", "OFFICER", "ADMIN").contains(role);
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override public int getOrder() { return -100; }
}