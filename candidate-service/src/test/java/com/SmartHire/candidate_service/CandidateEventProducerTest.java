package com.SmartHire.candidate_service;

import com.SmartHire.candidate_service.Service.CandidateEventProducer;
import com.SmartHire.candidate_service.dto.CandidateAppliedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CandidateEventProducer producer;

    private CandidateAppliedEvent event;

    @BeforeEach
    void setUp() {
        event = new CandidateAppliedEvent(1, 2);
    }

    @Test
    void testPublishCandidateAppliedEvent_Success() throws Exception {

        String json = "{\"candidateId\":1,\"jobId\":2}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(json);

        producer.publishCandidateAppliedEvent(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("arshdeep", json);
    }

    @Test
    void testPublishCandidateAppliedEvent_ObjectMapperThrowsException() throws Exception {

        when(objectMapper.writeValueAsString(event))
                .thenThrow(new RuntimeException("Serialization Failed"));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> producer.publishCandidateAppliedEvent(event));

        assertEquals(
                "Failed to publish Kafka event",
                exception.getMessage());

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void testPublishCandidateAppliedEvent_KafkaThrowsException() throws Exception {

        String json = "{\"candidateId\":1,\"jobId\":2}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(json);

        doThrow(new RuntimeException("Kafka Failure"))
                .when(kafkaTemplate)
                .send("arshdeep", json);

        assertThrows(
                RuntimeException.class,
                () -> producer.publishCandidateAppliedEvent(event));

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("arshdeep", json);
    }
}