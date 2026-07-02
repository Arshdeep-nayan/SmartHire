package com.smarthire.AI_screening_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "screening_results")
public class ScreeningResult {

    @Id
    private String id;

    private int candidateId;
    private int jobId;
    private int score;
    private String feedback;
    private LocalDateTime screenedAt;
}