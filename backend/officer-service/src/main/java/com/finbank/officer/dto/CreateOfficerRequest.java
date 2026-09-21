package com.finbank.officer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOfficerRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 120) String branchCode
) {}