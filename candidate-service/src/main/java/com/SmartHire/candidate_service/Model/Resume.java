package com.SmartHire.candidate_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resumes")
public class Resume {

    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    private int candidateId;

    private String resumeText;

    @CreatedDate
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime uploadedAt;
}