package com.SmartHire.candidate_service.Service;

import com.SmartHire.candidate_service.dto.CandidateAppliedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

            System.out.println("Published Event : " + json);

            kafkaTemplate.send("arshdeep", json);
        }
        catch (Exception e) {
            throw new RuntimeException(
                    "Failed to publish Kafka event", e);
        }
    }
}