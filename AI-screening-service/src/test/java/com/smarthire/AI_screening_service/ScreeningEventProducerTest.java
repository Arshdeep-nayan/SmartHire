package com.smarthire.AI_screening_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.AI_screening_service.dto.ScreeningCompletedEvent;
import com.smarthire.AI_screening_service.service.ScreeningEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ScreeningEventProducer producer;

    private ScreeningCompletedEvent event;

    @BeforeEach
    void setUp() {

        event = new ScreeningCompletedEvent();
        event.setCandidateId(1);
        event.setJobId(10);
        event.setScore(92);
    }

    @Test
    void testPublishScreeningCompletedEvent_Success() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10,\"score\":92}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(json);

        producer.publishScreeningCompletedEvent(event);

        verify(objectMapper)
                .writeValueAsString(event);

        verify(kafkaTemplate)
                .send("screening-completed", json);
    }

    @Test
    void testPublishScreeningCompletedEvent_ObjectMapperThrowsException() throws Exception {

        when(objectMapper.writeValueAsString(event))
                .thenThrow(new RuntimeException("Serialization Failed"));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> producer.publishScreeningCompletedEvent(event));

        assertEquals(
                "Failed to publish screening event",
                exception.getMessage());

        verify(objectMapper)
                .writeValueAsString(event);

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void testPublishScreeningCompletedEvent_KafkaThrowsException() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10,\"score\":92}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(json);

        doThrow(new RuntimeException("Kafka Failure"))
                .when(kafkaTemplate)
                .send("screening-completed", json);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> producer.publishScreeningCompletedEvent(event));

        assertEquals(
                "Failed to publish screening event",
                exception.getMessage());

        verify(objectMapper)
                .writeValueAsString(event);

        verify(kafkaTemplate)
                .send("screening-completed", json);
    }
}