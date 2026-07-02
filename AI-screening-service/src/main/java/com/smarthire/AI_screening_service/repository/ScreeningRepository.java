package com.smarthire.AI_screening_service.repository;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreeningRepository extends MongoRepository<ScreeningResult, String>
{
    Optional<ScreeningResult> findByCandidateId(int candidateId);
}