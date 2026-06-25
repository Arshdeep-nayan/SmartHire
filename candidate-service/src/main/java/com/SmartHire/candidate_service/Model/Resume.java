package com.SmartHire.candidate_service.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resumes")
public class Resume
{
    @Id
    private String id;
    private int candidateId;
    private String resumeText;
    private LocalDateTime uploadedAt;
}