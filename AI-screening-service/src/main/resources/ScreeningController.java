package com.smarthire.AI_screening_service.controller;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.service.ScreeningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/screening")
public class ScreeningController {

    private final ScreeningService service;

    public ScreeningController(ScreeningService service) {
        this.service = service;
    }

    // Trigger AI screening for a candidate against a specific job
    @PostMapping("/{candidateId}/{jobId}")
    public ResponseEntity<ScreeningResult> screenCandidate(
            @PathVariable int candidateId,
            @PathVariable int jobId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.screenCandidate(candidateId, jobId));
    }
}