package com.finbank.notification.service;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.dto.NotificationResponse;
import com.finbank.notification.entity.Notification;
import com.finbank.notification.entity.NotificationStatus;
import com.finbank.notification.exception.NotificationException;
import com.finbank.notification.exception.NotificationNotFoundException;
import com.finbank.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .notificationReference(generateReference())
                .customerNumber(request.getCustomerNumber().trim().toUpperCase())
                .type(request.getType())
                .channel(request.getChannel())
                .title(request.getTitle().trim())
                .message(request.getMessage().trim())
                .relatedReference(normalize(request.getRelatedReference()))
                .status(NotificationStatus.CREATED)
                .build();
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(String reference) {
        return toResponse(find(reference));
    }

    @Override
    public NotificationResponse markAsSent(String reference) {
        Notification notification = find(reference);
        if (notification.getStatus() == NotificationStatus.READ) {
            throw new NotificationException("Read notification cannot be marked as sent");
        }
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        return toResponse(notification);
    }

    @Override
    public NotificationResponse markAsRead(String reference) {
        Notification notification = find(reference);
        if (notification.getStatus() == NotificationStatus.CREATED) {
            notification.setStatus(NotificationStatus.READ);
            notification.setSentAt(LocalDateTime.now());
        } else if (notification.getStatus() == NotificationStatus.SENT) {
            notification.setStatus(NotificationStatus.READ);
        } else if (notification.getStatus() == NotificationStatus.FAILED) {
            throw new NotificationException("Failed notification cannot be marked as read");
        }
        notification.setReadAt(LocalDateTime.now());
        return toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getCustomerNotifications(String customerNumber) {
        return notificationRepository.findByCustomerNumberOrderByCreatedAtDesc(normalizeCustomer(customerNumber))
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getCustomerNotificationsByStatus(String customerNumber, NotificationStatus status) {
        return notificationRepository.findByCustomerNumberAndStatusOrderByCreatedAtDesc(normalizeCustomer(customerNumber), status)
                .stream().map(this::toResponse).toList();
    }

    private Notification find(String reference) {
        return notificationRepository.findByNotificationReference(reference)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + reference));
    }

    private String generateReference() {
        return "FNOT" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeCustomer(String value) {
        if (value == null || value.isBlank()) {
            throw new NotificationException("Customer number is required");
        }
        return value.trim().toUpperCase();
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationReference(n.getNotificationReference())
                .customerNumber(n.getCustomerNumber())
                .type(n.getType())
                .channel(n.getChannel())
                .title(n.getTitle())
                .message(n.getMessage())
                .status(n.getStatus())
                .relatedReference(n.getRelatedReference())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .readAt(n.getReadAt())
                .build();
    }
}