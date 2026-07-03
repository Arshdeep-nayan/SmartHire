package com.smarthire.AI_screening_service.feign;

import com.smarthire.AI_screening_service.dto.JobDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "JOB-SERVICE")
public interface JobFeignClient {

    @GetMapping("/jobs/job/{id}")
    JobDTO getJobById(@PathVariable("id") int id);
}