package com.smarthire.job_service.service;

import com.smarthire.job_service.model.job;
import com.smarthire.job_service.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class JobService
{
    private final JobRepository repo;

    public JobService(JobRepository repo)
    {
        this.repo = repo;
    }

    // Fetch all jobs from the database
    public List<job> getAllJobs()
    {
        return repo.findAll();
    }

    // Fetch a specific job using its ID
    public job getJobById(int id)
    {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // Create and save a new job
    public job createJob(job newJob)
    {
        return repo.save(newJob);
    }

    // Update an existing job's details
    public job updateJob(job updatedJob)
    {
        job existingJob = repo.findById(updatedJob.getId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setCompany(updatedJob.getCompany());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setSalary(updatedJob.getSalary());
        existingJob.setJobType(updatedJob.getJobType());
        existingJob.setExperience(updatedJob.getExperience());
        existingJob.setSkills(updatedJob.getSkills());
        existingJob.setActive(updatedJob.isActive());

        return repo.save(existingJob);
    }

    // Delete a job by its ID
    public void deleteJob(int id)
    {
        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        repo.delete(existingJob);
    }

    // Mark a job as active/open
    public job activateJob(int id)
    {
        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setActive(true);

        return repo.save(existingJob);
    }

    // Mark a job as inactive/closed
    public job deactivateJob(int id)
    {
        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setActive(false);

        return repo.save(existingJob);
    }

    // Return jobs page-by-page
    public Page<job> getJobsPaginated(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        return repo.findAll(pageable);
    }

    // Return all jobs sorted by salary
    public List<job> getJobsSortedBySalary()
    {
        return repo.findAll(Sort.by("salary"));
    }

    // Return jobs sorted by latest posting date
    public List<job> getJobsSortedByPostedAt()
    {
        return repo.findAll(
                Sort.by(Sort.Direction.DESC, "postedAt"));
    }

    // Search jobs using a keyword
    public List<job> searchJobs(String keyword)
    {
        return repo.searchjobs(keyword);
    }

    // Fetch jobs from a specific location
    public List<job> getJobsByLocation(String location)
    {
        return repo.findByLocationIgnoreCase(location);
    }

    // Fetch jobs from a specific company
    public List<job> getJobsByCompany(String company)
    {
        return repo.findByCompanyIgnoreCase(company);
    }

    // Fetch jobs within a salary range
    public List<job> getJobsBySalaryRange(double minSalary, double maxSalary)
    {
        return repo.findBySalaryBetween(minSalary, maxSalary);
    }

    // Fetch only active jobs
    public List<job> getActiveJobs()
    {
        return repo.findByIsActiveTrue();
    }

    // Fetch jobs by employment type
    public List<job> getJobsByJobType(String jobType)
    {
        return repo.findByJobTypeIgnoreCase(jobType);
    }

    // Fetch jobs by experience level
    public List<job> getJobsByExperience(String experience)
    {
        return repo.findByExperienceIgnoreCase(experience);
    }

}
