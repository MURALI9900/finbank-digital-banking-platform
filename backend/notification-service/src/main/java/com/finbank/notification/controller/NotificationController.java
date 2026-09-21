package com.finbank.notification.controller;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.dto.NotificationResponse;
import com.finbank.notification.entity.NotificationStatus;
import com.finbank.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<NotificationResponse> get(@PathVariable String reference) {
        return ResponseEntity.ok(notificationService.getNotification(reference));
    }

    @PutMapping("/{reference}/sent")
    public ResponseEntity<NotificationResponse> markSent(@PathVariable String reference) {
        return ResponseEntity.ok(notificationService.markAsSent(reference));
    }

    @PutMapping("/{reference}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable String reference) {
        return ResponseEntity.ok(notificationService.markAsRead(reference));
    }

    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<NotificationResponse>> getCustomer(@PathVariable String customerNumber) {
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customerNumber));
    }

    @GetMapping("/customer/{customerNumber}/status/{status}")
    public ResponseEntity<List<NotificationResponse>> getCustomerByStatus(
            @PathVariable String customerNumber,
            @PathVariable NotificationStatus status) {
        return ResponseEntity.ok(notificationService.getCustomerNotificationsByStatus(customerNumber, status));
    }
}