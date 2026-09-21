package com.finbank.notification.dto;

import com.finbank.notification.entity.NotificationChannel;
import com.finbank.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNotificationRequest {
    @NotBlank
    @Size(max = 30)
    private String customerNumber;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String message;

    @Size(max = 100)
    private String relatedReference;
}