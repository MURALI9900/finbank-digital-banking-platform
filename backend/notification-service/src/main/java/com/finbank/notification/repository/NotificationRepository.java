package com.finbank.notification.repository;

import com.finbank.notification.entity.Notification;
import com.finbank.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByNotificationReference(String notificationReference);
    List<Notification> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
    List<Notification> findByCustomerNumberAndStatusOrderByCreatedAtDesc(String customerNumber, NotificationStatus status);
}