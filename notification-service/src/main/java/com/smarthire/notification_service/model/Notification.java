package com.smarthire.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private int candidateId;

    private int jobId;

    private String message;

    private NotificationType type;

    private boolean read = false;

    private LocalDateTime createdAt;

    public enum NotificationType {
        APPLICATION_SUBMITTED,
        SCREENING_COMPLETED,
        SHORTLISTED,
        HIRED,
        JOB_POSTED
    }
}