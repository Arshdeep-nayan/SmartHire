package com.smarthire.notification_service.dto;

import com.smarthire.notification_service.model.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private int userId;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
}