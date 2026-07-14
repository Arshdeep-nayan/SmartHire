package com.SmartHire.candidate_service.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String skills;
    private String experience;
    private String currentLocation;

    public enum CandidateStatus
    {
        ACTIVE,
        INACTIVE,
        HIRED
    }

    @Enumerated(EnumType.STRING)
    private CandidateStatus status = CandidateStatus.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    public void onCreate()
    {
        registeredAt = LocalDateTime.now();
    }
}