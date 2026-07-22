package com.smarthire.notification_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.notification_service.dto.ScreeningCompletedEvent;
import com.smarthire.notification_service.model.Notification;
import com.smarthire.notification_service.service.NotificationService;
import com.smarthire.notification_service.service.ScreeningCompletedConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningCompletedConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ScreeningCompletedConsumer consumer;

    private ScreeningCompletedEvent event;

    @BeforeEach
    void setUp() {

        event = new ScreeningCompletedEvent();
        event.setCandidateId(1);
        event.setJobId(10);
        event.setScore(92);
    }

    @Test
    void testConsume_Success() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10,\"score\":92}";

        when(objectMapper.readValue(
                json,
                ScreeningCompletedEvent.class))
                .thenReturn(event);

        consumer.consume(json);

        verify(objectMapper)
                .readValue(
                        json,
                        ScreeningCompletedEvent.class);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationService)
                .createNotification(captor.capture());

        Notification notification =
                captor.getValue();

        assertEquals(1, notification.getCandidateId());
        assertEquals(10, notification.getJobId());

        assertEquals(
                "Your screening for job id 10 has been completed. Score : 92",
                notification.getMessage());

        assertEquals(
                Notification.NotificationType.SCREENING_COMPLETED,
                notification.getType());

        assertEquals(false, notification.isRead());
    }

    @Test
    void testConsume_InvalidJson() throws Exception {

        String json = "invalid-json";

        when(objectMapper.readValue(
                json,
                ScreeningCompletedEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        consumer.consume(json);

        verify(objectMapper)
                .readValue(
                        json,
                        ScreeningCompletedEvent.class);

        verify(notificationService, never())
                .createNotification(any(Notification.class));
    }

    @Test
    void testConsume_NotificationServiceThrowsException() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10,\"score\":92}";

        when(objectMapper.readValue(
                json,
                ScreeningCompletedEvent.class))
                .thenReturn(event);

        doThrow(new RuntimeException("Database Error"))
                .when(notificationService)
                .createNotification(any(Notification.class));

        consumer.consume(json);

        verify(notificationService)
                .createNotification(any(Notification.class));
    }
}