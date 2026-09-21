package com.finbank.beneficiary.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, int status, String message, String path) {}