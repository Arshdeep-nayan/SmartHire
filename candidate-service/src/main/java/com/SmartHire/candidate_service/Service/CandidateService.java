package com.SmartHire.candidate_service.Service;

import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Candidate;
import com.SmartHire.candidate_service.Repository.CandidateRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CandidateService
{
    private final CandidateRepository repo;
    public CandidateService(CandidateRepository repo){
        this.repo = repo;
    }

    public List<Candidate> getAllCandidates()
    {
       return repo.findAll();
    }

    public Candidate getCandidateById(int id)
    {
        return repo.findById(id).orElseThrow(()->new CandidateNotFoundException("Candidate not found"));
    }

    public Candidate registerCandidate(Candidate candidate)
    {
        return repo.save(candidate);
    }

    public Candidate updateCandidate(Candidate candidate)
    {
        Candidate a = repo.findById(candidate.getId()).orElseThrow(()->new CandidateNotFoundException("Candidate you are trying to update doesn't exists!!"));
        a.setName(candidate.getName());
        a.setEmail(candidate.getEmail());
        a.setPhone(candidate.getPhone());
        a.setSkills(candidate.getSkills());
        a.setCurrentLocation(candidate.getCurrentLocation());
        a.setExperience(candidate.getExperience());
        return repo.save(a);
    }

    public void deleteCandidate(int id)
    {
        Candidate a = repo.findById(id).orElseThrow(()->new CandidateNotFoundException("Candidate you are trying to delete doesn't exists!!"));
        repo.deleteById(id);
    }


    public List<Candidate> searchByKeyword(String keyword)
    {
        return repo.searchByKeyword(keyword);
    }


    public List<Candidate> getCandidatesBySkill(String skill)
    {
        return repo.findBySkillsContainingIgnoreCase(skill);
    }


    public List<Candidate> getCandidatesByLocation(String location)
    {
        return repo.findByCurrentLocationIgnoreCase(location);
    }


    public List<Candidate> getCandidatesByExperience(String experience)
    {
        return repo.findByExperienceIgnoreCase(experience);
    }

    public List<Candidate> getCandidatesByStatus(
            Candidate.CandidateStatus status)
    {
        return repo.findByStatus(status);
    }

    public Candidate activateCandidate(int id)
    {
        Candidate a = repo.findById(id).orElseThrow(()-> new CandidateNotFoundException("Candidate not found"));
        a.setStatus(Candidate.CandidateStatus.ACTIVE);
        return repo.save(a);
    }

    public Candidate deactivateCandidate(int id)
    {
        Candidate a = repo.findById(id).orElseThrow(()-> new CandidateNotFoundException("Candidate not found"));
        a.setStatus(Candidate.CandidateStatus.INACTIVE);
        return repo.save(a);
    }

    public Candidate markCandidateAsHired(int id)
    {
        Candidate a = repo.findById(id).orElseThrow(() -> new CandidateNotFoundException("Candidate not found"));

        a.setStatus(Candidate.CandidateStatus.HIRED);

        return repo.save(a);
    }


    public Page<Candidate> getCandidatesPaginated(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        return repo.findAll(pageable);
    }

    public List<Candidate> getCandidatesSortedByDate()
    {
        return repo.findAll(Sort.by(Sort.Direction.DESC,"registeredAt"));
    }



}
