package com.SmartHire.candidate_service.Service;

import com.SmartHire.candidate_service.dto.CandidateAppliedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CandidateEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CandidateEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishCandidateAppliedEvent(
            CandidateAppliedEvent event) {

        try {

            String json =
                    objectMapper.writeValueAsString(event);

            log.info("Publishing candidate applied event: {}", json);

            kafkaTemplate.send("arshdeep", json);

            log.info("Candidate applied event published successfully");

        }
        catch (Exception e) {

            log.error("Failed to publish candidate applied event", e);

            throw new RuntimeException(
                    "Failed to publish Kafka event",
                    e);
        }
    }
}