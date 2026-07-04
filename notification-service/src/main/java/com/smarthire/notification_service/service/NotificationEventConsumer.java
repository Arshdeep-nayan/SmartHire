package com.smarthire.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.notification_service.dto.CandidateAppliedEvent;
import com.smarthire.notification_service.model.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationEventConsumer(
            NotificationService notificationService,
            ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "arshdeep",
            groupId = "notification-group"
    )
    public void consume(String message) {

        try {
            System.out.println(
                    "Received Message : " + message);

            CandidateAppliedEvent event =
                    objectMapper.readValue(
                            message,
                            CandidateAppliedEvent.class);

            System.out.println(
                    "Received Event : " + event);

            Notification notification =
                    new Notification();

            notification.setUserId(
                    event.getCandidateId());

            notification.setMessage(
                    "You have successfully applied for job id "
                            + event.getJobId() + ".");

            notification.setType(
                    Notification.NotificationType
                            .APPLICATION_SUBMITTED);

            notification.setRead(false);

            notificationService
                    .createNotification(notification);

            System.out.println(
                    "Notification created successfully.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}