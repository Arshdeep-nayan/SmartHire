package com.smarthire.AI_screening_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private int id;
    private String title;
    private String company;
    private String location;
    private String description;
    private double salary;
    private String jobType;
    private String experience;
    private String skills;
    private boolean active;
}