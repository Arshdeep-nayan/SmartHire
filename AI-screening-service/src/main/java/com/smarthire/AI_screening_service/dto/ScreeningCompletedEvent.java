package com.smarthire.AI_screening_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningCompletedEvent {

    private int candidateId;
    private int jobId;
    private int score;
}