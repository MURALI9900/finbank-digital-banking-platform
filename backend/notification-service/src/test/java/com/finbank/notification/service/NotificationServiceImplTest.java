package com.finbank.notification.service;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.entity.NotificationChannel;
import com.finbank.notification.entity.NotificationStatus;
import com.finbank.notification.entity.NotificationType;
import com.finbank.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotificationCreatesCreatedNotification() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setCustomerNumber("FB1001");
        request.setType(NotificationType.TRANSACTION);
        request.setChannel(NotificationChannel.IN_APP);
        request.setTitle("Transfer successful");
        request.setMessage("Your transfer was completed.");
        request.setRelatedReference("FT123");

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = notificationService.createNotification(request);

        assertNotNull(response.getNotificationReference());
        assertEquals("FB1001", response.getCustomerNumber());
        assertEquals(NotificationStatus.CREATED, response.getStatus());
        assertEquals(NotificationType.TRANSACTION, response.getType());
    }

    @Test
    void markAsReadMovesSentNotificationToRead() {
        var notification = com.finbank.notification.entity.Notification.builder()
                .notificationReference("FNOT123")
                .customerNumber("FB1001")
                .type(NotificationType.SECURITY)
                .channel(NotificationChannel.IN_APP)
                .title("Security alert")
                .message("New login detected")
                .status(NotificationStatus.SENT)
                .build();

        when(notificationRepository.findByNotificationReference("FNOT123")).thenReturn(java.util.Optional.of(notification));

        var response = notificationService.markAsRead("FNOT123");

        assertEquals(NotificationStatus.READ, response.getStatus());
        assertNotNull(response.getReadAt());
    }
}