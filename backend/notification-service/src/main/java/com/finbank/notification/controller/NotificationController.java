package com.finbank.notification.controller;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.dto.NotificationResponse;
import com.finbank.notification.entity.NotificationStatus;
import com.finbank.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody CreateNotificationRequest request,
            Authentication authentication) {
        if (isCustomer(authentication) && !customerNumber(authentication).equalsIgnoreCase(request.getCustomerNumber())) {
            throw new AccessDeniedException("Customer can only create notifications for their own customer number");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<NotificationResponse> get(
            @PathVariable String reference,
            Authentication authentication) {
        NotificationResponse response = notificationService.getNotification(reference);
        authorizeCustomer(authentication, response.getCustomerNumber());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reference}/sent")
    public ResponseEntity<NotificationResponse> markSent(
            @PathVariable String reference,
            Authentication authentication) {
        requireOfficerOrAdmin(authentication);
        return ResponseEntity.ok(notificationService.markAsSent(reference));
    }

    @PutMapping("/{reference}/read")
    public ResponseEntity<NotificationResponse> markRead(
            @PathVariable String reference,
            Authentication authentication) {
        NotificationResponse response = notificationService.getNotification(reference);
        authorizeCustomer(authentication, response.getCustomerNumber());
        return ResponseEntity.ok(notificationService.markAsRead(reference));
    }

    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<NotificationResponse>> getCustomer(
            @PathVariable String customerNumber,
            Authentication authentication) {
        authorizeCustomer(authentication, customerNumber);
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customerNumber));
    }

    @GetMapping("/customer/{customerNumber}/status/{status}")
    public ResponseEntity<List<NotificationResponse>> getCustomerByStatus(
            @PathVariable String customerNumber,
            @PathVariable NotificationStatus status,
            Authentication authentication) {
        authorizeCustomer(authentication, customerNumber);
        return ResponseEntity.ok(notificationService.getCustomerNotificationsByStatus(customerNumber, status));
    }

    private void authorizeCustomer(Authentication authentication, String resourceCustomerNumber) {
        if (isCustomer(authentication) && !customerNumber(authentication).equalsIgnoreCase(resourceCustomerNumber)) {
            throw new AccessDeniedException("Customer can only access their own notifications");
        }
    }

    private void requireOfficerOrAdmin(Authentication authentication) {
        if (authentication == null || (!hasRole(authentication, "OFFICER") && !hasRole(authentication, "ADMIN"))) {
            throw new AccessDeniedException("Only officers and admins can mark notifications as sent");
        }
    }

    private boolean isCustomer(Authentication authentication) {
        return hasRole(authentication, "CUSTOMER");
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> ("ROLE_" + role).equals(authority.getAuthority()));
    }

    private String customerNumber(Authentication authentication) {
        String principal = authentication.getName();
        int separator = principal.indexOf('|');
        return separator >= 0 ? principal.substring(separator + 1) : "";
    }
}