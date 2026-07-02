package com.smarthire.AI_screening_service.controller;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.service.ScreeningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@CrossOrigin
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(
            ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @PostMapping("/screen")
    public ResponseEntity<ScreeningResult> screenCandidate(
            @RequestParam int candidateId,
            @RequestParam int jobId) {

        ScreeningResult result =
                screeningService.screenCandidate(
                        candidateId,
                        jobId);

        return new ResponseEntity<>(
                result,
                HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScreeningResult>>
    getAllScreenings() {

        return ResponseEntity.ok(
                screeningService.getAllScreenings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResult>
    getScreeningById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                screeningService.getScreeningById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ScreeningResult>>
    getScreeningByCandidateId(
            @PathVariable int candidateId) {

        return ResponseEntity.ok(
                screeningService
                        .getScreeningByCandidateId(
                                candidateId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ScreeningResult>>
    getScreeningByJobId(
            @PathVariable int jobId) {

        return ResponseEntity.ok(
                screeningService
                        .getScreeningByJobId(
                                jobId));
    }

    @GetMapping("/candidate/{candidateId}/job/{jobId}")
    public ResponseEntity<ScreeningResult>
    getScreeningByCandidateAndJob(
            @PathVariable int candidateId,
            @PathVariable int jobId) {

        return ResponseEntity.ok(
                screeningService
                        .getScreeningByCandidateAndJob(
                                candidateId,
                                jobId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteScreening(
            @PathVariable String id) {

        screeningService.deleteScreening(id);

        return ResponseEntity.ok(
                "Screening deleted successfully");
    }
}