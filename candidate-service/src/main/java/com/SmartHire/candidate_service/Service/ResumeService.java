package com.SmartHire.candidate_service.Service;

import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Resume;
import com.SmartHire.candidate_service.Repository.ResumeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ResumeService {

    private final ResumeRepository rrepo;

    public ResumeService(ResumeRepository rrepo) {
        this.rrepo = rrepo;
    }

    public Resume saveResume(Resume res) {

        log.info("Saving resume for candidate id: {}", res.getCandidateId());

        Resume savedResume = rrepo.save(res);

        log.info("Resume saved successfully for candidate id: {}", savedResume.getCandidateId());

        return savedResume;
    }

    public Resume getResumeByCandidateId(int candidateId) {

        log.info("Fetching resume for candidate id: {}", candidateId);

        Resume resume = rrepo.findByCandidateId(candidateId)
                .orElseThrow(() ->
                        new CandidateNotFoundException(
                                "Resume not found for this candidate"));

        log.info("Resume fetched successfully for candidate id: {}", candidateId);

        return resume;
    }

    public Resume updateResume(int candidateId, String newResumeText) {

        log.info("Updating resume for candidate id: {}", candidateId);

        Resume existing = getResumeByCandidateId(candidateId);

        existing.setResumeText(newResumeText);
        existing.setUploadedAt(LocalDateTime.now());

        Resume updatedResume = rrepo.save(existing);

        log.info("Resume updated successfully for candidate id: {}", candidateId);

        return updatedResume;
    }

    public void deleteResume(String id) {

        log.info("Deleting resume with id: {}", id);

        rrepo.deleteById(id);

        log.info("Resume deleted successfully with id: {}", id);
    }

}