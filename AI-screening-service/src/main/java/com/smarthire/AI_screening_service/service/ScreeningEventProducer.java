package com.smarthire.AI_screening_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthire.AI_screening_service.dto.ScreeningCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScreeningEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ScreeningEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishScreeningCompletedEvent(
            ScreeningCompletedEvent event) {

        try {

            String json =
                    objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    "screening-completed",
                    json);

            log.info("Published Screening Event: {}", json);

        }
        catch (Exception e) {

            log.error("Failed to publish screening event.", e);

            throw new RuntimeException(
                    "Failed to publish screening event",
                    e);
        }
    }
}