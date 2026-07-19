package com.SmartHire.candidate_service.Controller;

import com.SmartHire.candidate_service.Model.Candidate;
import com.SmartHire.candidate_service.Service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    @GetMapping("/candidates/all")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(service.getAllCandidates());
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable int id) {
        return ResponseEntity.ok(service.getCandidateById(id));
    }

    @PostMapping("/candidate/add")
    public ResponseEntity<Candidate> registerCandidate(@Valid @RequestBody Candidate candidate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerCandidate(candidate));
    }

    @PutMapping("/candidate/update/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable int id, @Valid @RequestBody Candidate candidate) {
        return ResponseEntity.ok(service.updateCandidate(id, candidate));
    }

    @DeleteMapping("/candidate/delete/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable int id) {
        service.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidate/search")
    public ResponseEntity<List<Candidate>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchByKeyword(keyword));
    }

    @GetMapping("/candidate/skill/{skill}")
    public ResponseEntity<List<Candidate>> getCandidatesBySkill(@PathVariable String skill) {
        return ResponseEntity.ok(service.getCandidatesBySkill(skill));
    }

    @GetMapping("/candidate/location/{location}")
    public ResponseEntity<List<Candidate>> getCandidatesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(service.getCandidatesByLocation(location));
    }

    @GetMapping("/candidate/experience/{experience}")
    public ResponseEntity<List<Candidate>> getCandidatesByExperience(@PathVariable String experience) {
        return ResponseEntity.ok(service.getCandidatesByExperience(experience));
    }

    @GetMapping("/candidate/status/{status}")
    public ResponseEntity<List<Candidate>> getCandidatesByStatus(@PathVariable Candidate.CandidateStatus status) {
        return ResponseEntity.ok(service.getCandidatesByStatus(status));
    }

    @PatchMapping("/candidate/{id}/activate")
    public ResponseEntity<Candidate> activateCandidate(@PathVariable int id) {
        return ResponseEntity.ok(service.activateCandidate(id));
    }

    @PatchMapping("/candidate/{id}/deactivate")
    public ResponseEntity<Candidate> deactivateCandidate(@PathVariable int id) {
        return ResponseEntity.ok(service.deactivateCandidate(id));
    }

    @PatchMapping("/candidate/{id}/hire")
    public ResponseEntity<Candidate> markCandidateAsHired(@PathVariable int id) {
        return ResponseEntity.ok(service.markCandidateAsHired(id));
    }

    @GetMapping("/candidate/page")
    public ResponseEntity<Page<Candidate>> getCandidatesPaginated(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(service.getCandidatesPaginated(page, size));
    }

    @GetMapping("/candidate/sort/date")
    public ResponseEntity<List<Candidate>> getCandidatesSortedByDate() {
        return ResponseEntity.ok(service.getCandidatesSortedByDate());
    }

    @PostMapping("/candidate/apply")
    public ResponseEntity<String> applyForJob(@RequestParam int candidateId, @RequestParam int jobId) {
        service.applyForJob(candidateId, jobId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Application submitted successfully");
    }
}