package com.smarthire.AI_screening_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.AI_screening_service.dto.CandidateAppliedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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

            System.out.println(
                    "Received Message : " + message);

            CandidateAppliedEvent event;

            // Handle double serialized message
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

            System.out.println(
                    "Received Event : " + event);

            screeningService.screenCandidate(
                    event.getCandidateId(),
                    event.getJobId());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}