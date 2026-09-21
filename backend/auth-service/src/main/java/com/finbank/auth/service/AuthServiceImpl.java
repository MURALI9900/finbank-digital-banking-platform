package com.finbank.auth.service;

import com.finbank.auth.dto.AuthResponse;
import com.finbank.auth.dto.LoginRequest;
import com.finbank.auth.dto.RegisterRequest;
import com.finbank.auth.entity.UserAccount;
import com.finbank.auth.entity.UserStatus;
import com.finbank.auth.exception.AuthException;
import com.finbank.auth.repository.UserAccountRepository;
import com.finbank.auth.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserAccountRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase();
        if (repository.existsByUsername(username)) {
            throw new AuthException("Username already exists");
        }
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(com.finbank.auth.entity.Role.CUSTOMER);
        user.setCustomerNumber(request.customerNumber() == null || request.customerNumber().isBlank() ? null : request.customerNumber().trim());
        user.setStatus(UserStatus.ACTIVE);
        repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount user = repository.findByUsername(request.username().trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Invalid username or password"));
        if (user.getStatus() != UserStatus.ACTIVE || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid username or password");
        }
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name(), user.getCustomerNumber());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs() / 1000, user.getUsername(), user.getRole().name(), user.getCustomerNumber());
    }
}