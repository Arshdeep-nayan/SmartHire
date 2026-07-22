package com.smarthire.AI_screening_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.AI_screening_service.dto.CandidateAppliedEvent;
import com.smarthire.AI_screening_service.service.CandidateEventConsumer;
import com.smarthire.AI_screening_service.service.ScreeningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateEventConsumerTest {

    @Mock
    private ScreeningService screeningService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CandidateEventConsumer consumer;

    private CandidateAppliedEvent event;

    @BeforeEach
    void setUp() {

        event = new CandidateAppliedEvent();
        event.setCandidateId(1);
        event.setJobId(10);
    }

    @Test
    void testConsume_NormalJson() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10}";

        when(objectMapper.readValue(
                json,
                CandidateAppliedEvent.class))
                .thenReturn(event);

        consumer.consume(json);

        verify(objectMapper)
                .readValue(
                        json,
                        CandidateAppliedEvent.class);

        verify(screeningService)
                .screenCandidate(1, 10);
    }

    @Test
    void testConsume_DoubleEncodedJson() throws Exception {

        String wrappedJson =
                "\"{\\\"candidateId\\\":1,\\\"jobId\\\":10}\"";

        String actualJson =
                "{\"candidateId\":1,\"jobId\":10}";

        when(objectMapper.readValue(
                wrappedJson,
                String.class))
                .thenReturn(actualJson);

        when(objectMapper.readValue(
                actualJson,
                CandidateAppliedEvent.class))
                .thenReturn(event);

        consumer.consume(wrappedJson);

        verify(objectMapper)
                .readValue(
                        wrappedJson,
                        String.class);

        verify(objectMapper)
                .readValue(
                        actualJson,
                        CandidateAppliedEvent.class);

        verify(screeningService)
                .screenCandidate(1, 10);
    }

    @Test
    void testConsume_InvalidJson() throws Exception {

        String invalidJson = "invalid-json";

        when(objectMapper.readValue(
                invalidJson,
                CandidateAppliedEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        consumer.consume(invalidJson);

        verify(objectMapper)
                .readValue(
                        invalidJson,
                        CandidateAppliedEvent.class);

        verify(screeningService, never())
                .screenCandidate(anyInt(), anyInt());
    }

    @Test
    void testConsume_ScreeningServiceThrowsException() throws Exception {

        String json =
                "{\"candidateId\":1,\"jobId\":10}";

        when(objectMapper.readValue(
                json,
                CandidateAppliedEvent.class))
                .thenReturn(event);

        doThrow(new RuntimeException("AI Failure"))
                .when(screeningService)
                .screenCandidate(1, 10);

        consumer.consume(json);

        verify(screeningService)
                .screenCandidate(1, 10);
    }
}