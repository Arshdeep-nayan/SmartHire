package com.smarthire.AI_screening_service.repository;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningRepository extends MongoRepository<ScreeningResult, String> {

    List<ScreeningResult> findByCandidateId(int candidateId);

    List<ScreeningResult> findByJobIdOrderByScoreDesc(int jobId);

    Optional<ScreeningResult> findByCandidateIdAndJobId(
            int candidateId,
            int jobId);
}