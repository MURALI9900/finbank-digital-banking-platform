package com.finbank.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 4, max = 100) String username,
        @NotBlank @Size(min = 8, max = 100) String password,
        @Size(max = 20) String customerNumber
) {}