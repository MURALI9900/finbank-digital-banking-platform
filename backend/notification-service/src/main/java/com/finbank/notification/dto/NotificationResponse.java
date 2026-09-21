package com.finbank.notification.dto;

import com.finbank.notification.entity.NotificationChannel;
import com.finbank.notification.entity.NotificationStatus;
import com.finbank.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private String notificationReference;
    private String customerNumber;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private NotificationStatus status;
    private String relatedReference;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}