package com.smarthire.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.notification_service.dto.CandidateAppliedEvent;
import com.smarthire.notification_service.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
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

            log.info("Received Kafka message: {}", message);

            CandidateAppliedEvent event =
                    objectMapper.readValue(
                            message,
                            CandidateAppliedEvent.class);

            log.info(
                    "Candidate applied event received. Candidate ID: {}, Job ID: {}",
                    event.getCandidateId(),
                    event.getJobId());

            Notification notification =
                    new Notification();

            notification.setCandidateId(
                    event.getCandidateId());

            notification.setJobId(
                    event.getJobId());

            notification.setMessage(
                    "You have successfully applied for job id "
                            + event.getJobId() + ".");

            notification.setType(
                    Notification.NotificationType
                            .APPLICATION_SUBMITTED);

            notification.setRead(false);

            notificationService
                    .createNotification(notification);

            log.info(
                    "Notification created successfully for candidate id: {}",
                    event.getCandidateId());

        }
        catch (Exception e) {

            log.error("Failed to process candidate applied event", e);

        }
    }
}