package com.SmartHire.candidate_service.Service;

import com.SmartHire.candidate_service.Exception.CandidateAlreadyExistsException;
import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Candidate;
import com.SmartHire.candidate_service.Repository.CandidateRepository;
import com.SmartHire.candidate_service.dto.CandidateAppliedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CandidateService {

    private final CandidateRepository repo;
    private final CandidateEventProducer producer;

    public CandidateService(
            CandidateRepository repo,
            CandidateEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    public List<Candidate> getAllCandidates() {

        log.info("Fetching all candidates");

        List<Candidate> candidates = repo.findAll();

        log.info("Fetched {} candidates", candidates.size());

        return candidates;
    }

    public Candidate getCandidateById(int id) {

        log.info("Fetching candidate with id: {}", id);

        Candidate candidate = repo.findById(id)
                .orElseThrow(() ->
                        new CandidateNotFoundException("Candidate not found"));

        log.info("Candidate fetched successfully with id: {}", id);

        return candidate;
    }

    public Candidate registerCandidate(Candidate candidate)
    {
        log.info("Registering candidate: {}", candidate.getEmail());

        if (repo.findByEmail(candidate.getEmail()).isPresent())
        {
            log.warn(
                    "Candidate already exists with email: {}",
                    candidate.getEmail());

            throw new CandidateAlreadyExistsException(
                    "Candidate already exists with email: "
                            + candidate.getEmail());
        }

        Candidate savedCandidate = repo.save(candidate);

        log.info(
                "Candidate registered successfully with id: {}",
                savedCandidate.getId());

        return savedCandidate;
    }

    public Candidate updateCandidate(Candidate candidate) {

        log.info("Updating candidate with id: {}", candidate.getId());

        Candidate a = repo.findById(candidate.getId())
                .orElseThrow(() ->
                        new CandidateNotFoundException(
                                "Candidate you are trying to update doesn't exists!!"));

        a.setName(candidate.getName());
        a.setEmail(candidate.getEmail());
        a.setPhone(candidate.getPhone());
        a.setSkills(candidate.getSkills());
        a.setCurrentLocation(candidate.getCurrentLocation());
        a.setExperience(candidate.getExperience());

        Candidate updatedCandidate = repo.save(a);

        log.info("Candidate updated successfully with id: {}", updatedCandidate.getId());

        return updatedCandidate;
    }

    public void deleteCandidate(int id) {

        log.info("Deleting candidate with id: {}", id);

        Candidate a = repo.findById(id)
                .orElseThrow(() ->
                        new CandidateNotFoundException(
                                "Candidate you are trying to delete doesn't exists!!"));

        repo.deleteById(id);

        log.info("Candidate deleted successfully with id: {}", id);
    }

    public List<Candidate> searchByKeyword(String keyword) {

        log.info("Searching candidates with keyword: {}", keyword);

        List<Candidate> candidates = repo.searchByKeyword(keyword);

        log.info("Found {} candidates for keyword: {}", candidates.size(), keyword);

        return candidates;
    }

    public List<Candidate> getCandidatesBySkill(String skill) {

        log.info("Fetching candidates with skill: {}", skill);

        List<Candidate> candidates = repo.findBySkillsContainingIgnoreCase(skill);

        log.info("Found {} candidates with skill: {}", candidates.size(), skill);

        return candidates;
    }

    public List<Candidate> getCandidatesByLocation(String location) {

        log.info("Fetching candidates from location: {}", location);

        List<Candidate> candidates = repo.findByCurrentLocationIgnoreCase(location);

        log.info("Found {} candidates from location: {}", candidates.size(), location);

        return candidates;
    }

    public List<Candidate> getCandidatesByExperience(String experience) {

        log.info("Fetching candidates with experience: {}", experience);

        List<Candidate> candidates = repo.findByExperienceIgnoreCase(experience);

        log.info("Found {} candidates with experience: {}", candidates.size(), experience);

        return candidates;
    }

    public List<Candidate> getCandidatesByStatus(
            Candidate.CandidateStatus status) {

        log.info("Fetching candidates with status: {}", status);

        List<Candidate> candidates = repo.findByStatus(status);

        log.info("Found {} candidates with status: {}", candidates.size(), status);

        return candidates;
    }

    public Candidate activateCandidate(int id) {

        log.info("Activating candidate with id: {}", id);

        Candidate a = repo.findById(id)
                .orElseThrow(() ->
                        new CandidateNotFoundException("Candidate not found"));

        a.setStatus(Candidate.CandidateStatus.ACTIVE);

        Candidate updatedCandidate = repo.save(a);

        log.info("Candidate activated successfully with id: {}", id);

        return updatedCandidate;
    }

    public Candidate deactivateCandidate(int id) {

        log.info("Deactivating candidate with id: {}", id);

        Candidate a = repo.findById(id)
                .orElseThrow(() ->
                        new CandidateNotFoundException("Candidate not found"));

        a.setStatus(Candidate.CandidateStatus.INACTIVE);

        Candidate updatedCandidate = repo.save(a);

        log.info("Candidate deactivated successfully with id: {}", id);

        return updatedCandidate;
    }

    public Candidate markCandidateAsHired(int id) {

        log.info("Marking candidate as hired with id: {}", id);

        Candidate a = repo.findById(id)
                .orElseThrow(() ->
                        new CandidateNotFoundException("Candidate not found"));

        a.setStatus(Candidate.CandidateStatus.HIRED);

        Candidate updatedCandidate = repo.save(a);

        log.info("Candidate marked as hired successfully with id: {}", id);

        return updatedCandidate;
    }

    public Page<Candidate> getCandidatesPaginated(int page, int size) {

        log.info("Fetching candidates page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Candidate> candidates = repo.findAll(pageable);

        log.info("Fetched {} candidates on page {}", candidates.getNumberOfElements(), page);

        return candidates;
    }

    public List<Candidate> getCandidatesSortedByDate() {

        log.info("Fetching candidates sorted by registration date");

        List<Candidate> candidates = repo.findAll(
                Sort.by(Sort.Direction.DESC, "registeredAt"));

        log.info("Fetched {} candidates sorted by registration date", candidates.size());

        return candidates;
    }


    public void applyForJob(int candidateId, int jobId) {

        log.info("Candidate {} is applying for job {}", candidateId, jobId);

        Candidate candidate = repo.findById(candidateId)
                .orElseThrow(() ->
                        new CandidateNotFoundException(
                                "Candidate not found"));

        CandidateAppliedEvent event =
                new CandidateAppliedEvent(
                        candidate.getId(),
                        jobId);

        producer.publishCandidateAppliedEvent(event);

        log.info("Candidate applied event published successfully for candidate {} and job {}",
                candidateId, jobId);
    }
}