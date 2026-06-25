package com.SmartHire.candidate_service.Controller;

import com.SmartHire.candidate_service.Model.Resume;
import com.SmartHire.candidate_service.Service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resumes")
public class ResumeController
{
    private final ResumeService service;
    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Resume> saveResume(@RequestBody Resume resume) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveResume(resume));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<Resume> getResumeByCandidateId(@PathVariable int candidateId) {
        return ResponseEntity.ok(service.getResumeByCandidateId(candidateId));
    }

    @PutMapping("/candidate/{candidateId}")
    public ResponseEntity<Resume> updateResume(@PathVariable int candidateId, @RequestBody String newResumeText) {
        return ResponseEntity.ok(service.updateResume(candidateId, newResumeText));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable String id) {
        service.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
}