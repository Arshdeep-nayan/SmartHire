package com.smarthire.AI_screening_service.feign;

import com.smarthire.AI_screening_service.dto.CandidateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CANDIDATE-SERVICE")
public interface CandidateFeignClient {

    @GetMapping("/api/candidate/{id}")
    CandidateDTO getCandidateById(@PathVariable int id);
}