package com.finbank.customer.config;

public record AuthenticatedUser(String username, String role, String customerNumber) {
}
