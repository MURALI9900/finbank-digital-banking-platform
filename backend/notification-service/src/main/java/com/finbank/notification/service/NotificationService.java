package com.finbank.notification.service;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.dto.NotificationResponse;
import com.finbank.notification.entity.NotificationStatus;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(CreateNotificationRequest request);
    NotificationResponse getNotification(String reference);
    NotificationResponse markAsSent(String reference);
    NotificationResponse markAsRead(String reference);
    List<NotificationResponse> getCustomerNotifications(String customerNumber);
    List<NotificationResponse> getCustomerNotificationsByStatus(String customerNumber, NotificationStatus status);
}