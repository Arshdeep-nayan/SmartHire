package com.smarthire.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.notification_service.dto.ScreeningCompletedEvent;
import com.smarthire.notification_service.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
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

            log.info("Received screening completed message: {}", message);

            ScreeningCompletedEvent event =
                    objectMapper.readValue(
                            message,
                            ScreeningCompletedEvent.class);

            log.info(
                    "Screening completed event received. Candidate ID: {}, Job ID: {}, Score: {}",
                    event.getCandidateId(),
                    event.getJobId(),
                    event.getScore());

            Notification notification =
                    new Notification();

            notification.setCandidateId(
                    event.getCandidateId());

            notification.setJobId(
                    event.getJobId());

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
                    .createNotification(notification);

            log.info(
                    "Screening completion notification created successfully for candidate id: {}",
                    event.getCandidateId());

        }
        catch (Exception e) {

            log.error("Failed to process screening completed event", e);

        }
    }
}