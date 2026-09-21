package com.finbank.notification.event;

import com.finbank.notification.dto.CreateNotificationRequest;
import com.finbank.notification.entity.NotificationChannel;
import com.finbank.notification.entity.NotificationType;
import com.finbank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "finbank.transaction.events", groupId = "notification-service", containerFactory = "kafkaListenerContainerFactory")
    public void handle(TransactionEvent event) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setCustomerNumber(event.customerNumber());
        request.setType(NotificationType.TRANSACTION);
        request.setChannel(NotificationChannel.IN_APP);
        request.setTitle("Transaction initiated");
        request.setMessage("Transaction " + event.transactionReference() + " for " + event.amount() + " " + event.currency() + " was initiated.");
        request.setRelatedReference(event.transactionReference());
        notificationService.createNotification(request);
    }
}