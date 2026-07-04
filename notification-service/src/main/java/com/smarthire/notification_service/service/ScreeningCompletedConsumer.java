package com.smarthire.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.notification_service.dto.ScreeningCompletedEvent;
import com.smarthire.notification_service.model.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ScreeningCompletedConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public ScreeningCompletedConsumer(
            NotificationService notificationService,
            ObjectMapper objectMapper) {

        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "screening-completed",
            groupId = "notification-group"
    )
    public void consume(String message) {

        try {

            System.out.println(
                    "Received Screening Message : "
                            + message);

            ScreeningCompletedEvent event =
                    objectMapper.readValue(
                            message,
                            ScreeningCompletedEvent.class);

            System.out.println(
                    "Received Screening Event : "
                            + event);

            Notification notification =
                    new Notification();

            notification.setUserId(
                    event.getCandidateId());

            notification.setMessage(
                    "Your screening for job id "
                            + event.getJobId()
                            + " has been completed. "
                            + "Score : "
                            + event.getScore());

            notification.setType(
                    Notification.NotificationType
                            .SCREENING_COMPLETED);

            notification.setRead(false);

            notificationService
                    .createNotification(
                            notification);

            System.out.println(
                    "Screening notification created successfully.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}