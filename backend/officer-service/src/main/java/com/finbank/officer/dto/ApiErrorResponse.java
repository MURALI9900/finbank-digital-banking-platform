package com.finbank.officer.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, int status, String message, String path) {}