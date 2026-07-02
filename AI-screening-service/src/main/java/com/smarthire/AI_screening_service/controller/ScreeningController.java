package com.smarthire.AI_screening_service.controller;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.service.ScreeningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screening")
@CrossOrigin
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @PostMapping("/screen")
    public ResponseEntity<ScreeningResult> screenCandidate(
            @RequestParam int candidateId,
            @RequestParam int jobId) {

        ScreeningResult result =
                screeningService.screenCandidate(candidateId, jobId);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}