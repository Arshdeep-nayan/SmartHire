package com.SmartHire.candidate_service.Repository;

import com.SmartHire.candidate_service.Model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer>
{
    @Query("select p from Candidate p Where LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(p.skills) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(p.currentLocation) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    List<Candidate> searchByKeyword(@Param("keyword") String keyword);

    List<Candidate> findBySkillsContainingIgnoreCase(String skill);

    List<Candidate> findByCurrentLocationIgnoreCase(String currentLocation);

    List<Candidate> findByExperienceIgnoreCase(String experience);

    List<Candidate> findByStatus(Candidate.CandidateStatus status);

    Optional<Candidate> findByEmail(String email);
}