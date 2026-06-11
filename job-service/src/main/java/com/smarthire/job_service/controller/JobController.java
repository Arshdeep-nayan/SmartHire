package com.smarthire.job_service.controller;

import com.smarthire.job_service.model.job;
import com.smarthire.job_service.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController
{
    private final JobService service;
    public JobController(JobService service)
    {
        this.service = service;
    }

    // Fetch all jobs
    @GetMapping("/jobs/all")
    public ResponseEntity<List<job>> getAllJobs()
    {
        return ResponseEntity.ok(service.getAllJobs());
    }

    // Fetch a specific job by ID
    @GetMapping("/job/{id}")
    public ResponseEntity<job> getJobById(@PathVariable int id)
    {
        return ResponseEntity.ok(service.getJobById(id));
    }

    // Create a new job
    @PostMapping("/job/add")
    public ResponseEntity<job> createJob(@Valid @RequestBody job newJob)
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createJob(newJob));
    }

    // Update an existing job
    @PutMapping("/job/update")
    public ResponseEntity<job> updateJob(@Valid @RequestBody job updatedJob)
    {
        return ResponseEntity.ok(service.updateJob(updatedJob));
    }

    // Delete a job by ID
    @DeleteMapping("/job/delete/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable int id)
    {
        service.deleteJob(id);

        return ResponseEntity.noContent().build();
    }

    // Activate a job posting
    @PatchMapping("/job/{id}/activate")
    public ResponseEntity<job> activateJob(@PathVariable int id)
    {
        return ResponseEntity.ok(service.activateJob(id));
    }

    // Deactivate a job posting
    @PatchMapping("/job/{id}/deactivate")
    public ResponseEntity<job> deactivateJob(@PathVariable int id)
    {
        return ResponseEntity.ok(service.deactivateJob(id));
    }

    // Search jobs using a keyword
    @GetMapping("/search")
    public ResponseEntity<List<job>> searchJobs(@RequestParam String keyword)
    {
        return ResponseEntity.ok(service.searchJobs(keyword));
    }

    // Fetch jobs by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<job>> getJobsByLocation(@PathVariable String location)
    {
        return ResponseEntity.ok(service.getJobsByLocation(location));
    }

    // Fetch jobs by company
    @GetMapping("/company/{company}")
    public ResponseEntity<List<job>> getJobsByCompany(@PathVariable String company)
    {
        return ResponseEntity.ok(service.getJobsByCompany(company));
    }

    // Fetch jobs within a salary range
    @GetMapping("/salary")
    public ResponseEntity<List<job>> getJobsBySalaryRange(@RequestParam double minSalary, @RequestParam double maxSalary)
    {
        return ResponseEntity.ok(service.getJobsBySalaryRange(minSalary, maxSalary));
    }

    // Fetch only active jobs
    @GetMapping("/active")
    public ResponseEntity<List<job>> getActiveJobs()
    {
        return ResponseEntity.ok(service.getActiveJobs());
    }

    // Fetch jobs by employment type
    @GetMapping("/type/{jobType}")
    public ResponseEntity<List<job>> getJobsByJobType(@PathVariable String jobType)
    {
        return ResponseEntity.ok(service.getJobsByJobType(jobType));
    }

    // Fetch jobs by experience level
    @GetMapping("/experience/{experience}")
    public ResponseEntity<List<job>> getJobsByExperience(@PathVariable String experience)
    {
        return ResponseEntity.ok(service.getJobsByExperience(experience));
    }

    // Fetch jobs page-by-page
    @GetMapping("/page")
    public ResponseEntity<Page<job>> getJobsPaginated(@RequestParam int page, @RequestParam int size)
    {
        return ResponseEntity.ok(service.getJobsPaginated(page, size));
    }

    // Fetch jobs sorted by salary
    @GetMapping("/sort/salary")
    public ResponseEntity<List<job>> getJobsSortedBySalary()
    {
        return ResponseEntity.ok(service.getJobsSortedBySalary());
    }

    // Fetch jobs sorted by latest posting date
    @GetMapping("/sort/date")
    public ResponseEntity<List<job>> getJobsSortedByPostedAt()
    {
        return ResponseEntity.ok(service.getJobsSortedByPostedAt());
    }

}
