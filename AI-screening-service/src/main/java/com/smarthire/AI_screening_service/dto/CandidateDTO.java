package com.smarthire.AI_screening_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String skills;
    private String experience;
    private String currentLocation;
    private String status;
}