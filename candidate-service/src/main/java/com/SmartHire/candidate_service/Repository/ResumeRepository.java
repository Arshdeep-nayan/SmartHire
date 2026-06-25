package com.SmartHire.candidate_service.Repository;

import com.SmartHire.candidate_service.Model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface ResumeRepository extends MongoRepository<Resume,String>
{
    Optional<Resume> findByCandidateId(int candidateId);
}
