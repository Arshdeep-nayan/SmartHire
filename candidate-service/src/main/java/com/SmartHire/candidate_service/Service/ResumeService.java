package com.SmartHire.candidate_service.Service;


import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Resume;
import com.SmartHire.candidate_service.Repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumeService
{
    private final ResumeRepository rrepo;
    public ResumeService(ResumeRepository rrepo) {
        this.rrepo = rrepo;
    }


    public Resume saveResume(Resume res)
    {
       return rrepo.save(res);
    }

    public Resume getResumeByCandidateId(int candidateId)
    {
       return rrepo.findByCandidateId(candidateId).orElseThrow(()->new CandidateNotFoundException("Resume not found for this candidate"));
    }

    public Resume updateResume(int candidateId, String newResumeText) {
        Resume existing = getResumeByCandidateId(candidateId);
        existing.setResumeText(newResumeText);
        existing.setUploadedAt(LocalDateTime.now());
        return rrepo.save(existing);
    }

    public void deleteResume(String id)
    {
        rrepo.deleteById(id);
    }

}
