package com.smarthire.job_service.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class job
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "location is required")
    private String location;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Description is required")
    private String description;


    private double salary;
    private String jobType;
    private String experience;
    private String skills;

    @Column(updatable = false)
    private String postedBy;

    @Column(updatable = false)
    private LocalDateTime postedAt;
    private boolean isActive = true;

    @PrePersist
    protected void onCreate(){
        postedAt = LocalDateTime.now();
    }

}
