package com.smarthire.AI_screening_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.AI_screening_service.dto.CandidateAppliedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CandidateEventConsumer {

    private final ScreeningService screeningService;
    private final ObjectMapper objectMapper;

    public CandidateEventConsumer(
            ScreeningService screeningService,
            ObjectMapper objectMapper) {

        this.screeningService = screeningService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "arshdeep",
            groupId = "screening-group"
    )
    public void consume(String message) {

        try {

            log.info("Received Kafka Message.");

            CandidateAppliedEvent event;

            if (message.startsWith("\"")) {

                String actualJson =
                        objectMapper.readValue(
                                message,
                                String.class);

                event =
                        objectMapper.readValue(
                                actualJson,
                                CandidateAppliedEvent.class);
            }
            else {

                event =
                        objectMapper.readValue(
                                message,
                                CandidateAppliedEvent.class);
            }

            log.info("Received Candidate Applied Event: {}", event);

            screeningService.screenCandidate(
                    event.getCandidateId(),
                    event.getJobId());

            log.info("Candidate screening completed successfully.");

        }
        catch (Exception e) {

            log.error("Error while processing Kafka message.", e);

        }
    }
}