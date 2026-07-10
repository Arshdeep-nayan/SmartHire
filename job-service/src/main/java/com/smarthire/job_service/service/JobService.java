package com.smarthire.job_service.service;

import com.smarthire.job_service.model.job;
import com.smarthire.job_service.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JobService {

    private final JobRepository repo;

    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    public List<job> getAllJobs() {

        log.info("Fetching all jobs");

        List<job> jobs = repo.findAll();

        log.info("Fetched {} jobs", jobs.size());

        return jobs;
    }

    public job getJobById(int id) {

        log.info("Fetching job with id: {}", id);

        job job = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        log.info("Job fetched successfully with id: {}", id);

        return job;
    }

    public job createJob(job newJob) {

        log.info("Creating job: {} at {}", newJob.getTitle(), newJob.getCompany());

        job savedJob = repo.save(newJob);

        log.info("Job created successfully with id: {}", savedJob.getId());

        return savedJob;
    }

    public job updateJob(job updatedJob) {

        log.info("Updating job with id: {}", updatedJob.getId());

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

        job savedJob = repo.save(existingJob);

        log.info("Job updated successfully with id: {}", savedJob.getId());

        return savedJob;
    }

    public void deleteJob(int id) {

        log.info("Deleting job with id: {}", id);

        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        repo.delete(existingJob);

        log.info("Job deleted successfully with id: {}", id);
    }

    public job activateJob(int id) {

        log.info("Activating job with id: {}", id);

        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setActive(true);

        job savedJob = repo.save(existingJob);

        log.info("Job activated successfully with id: {}", id);

        return savedJob;
    }

    public job deactivateJob(int id) {

        log.info("Deactivating job with id: {}", id);

        job existingJob = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setActive(false);

        job savedJob = repo.save(existingJob);

        log.info("Job deactivated successfully with id: {}", id);

        return savedJob;
    }

    public Page<job> getJobsPaginated(int page, int size) {

        log.info("Fetching jobs page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<job> jobs = repo.findAll(pageable);

        log.info("Fetched {} jobs on page {}", jobs.getNumberOfElements(), page);

        return jobs;
    }

    public List<job> getJobsSortedBySalary() {

        log.info("Fetching jobs sorted by salary");

        List<job> jobs = repo.findAll(Sort.by("salary"));

        log.info("Fetched {} jobs sorted by salary", jobs.size());

        return jobs;
    }

    public List<job> getJobsSortedByPostedAt() {

        log.info("Fetching jobs sorted by posted date");

        List<job> jobs = repo.findAll(
                Sort.by(Sort.Direction.DESC, "postedAt"));

        log.info("Fetched {} jobs sorted by posted date", jobs.size());

        return jobs;
    }

    public List<job> searchJobs(String keyword) {

        log.info("Searching jobs with keyword: {}", keyword);

        List<job> jobs = repo.searchjobs(keyword);

        log.info("Found {} jobs for keyword: {}", jobs.size(), keyword);

        return jobs;
    }

    public List<job> getJobsByLocation(String location) {

        log.info("Fetching jobs for location: {}", location);

        List<job> jobs = repo.findByLocationIgnoreCase(location);

        log.info("Found {} jobs for location: {}", jobs.size(), location);

        return jobs;
    }

    public List<job> getJobsByCompany(String company) {

        log.info("Fetching jobs for company: {}", company);

        List<job> jobs = repo.findByCompanyIgnoreCase(company);

        log.info("Found {} jobs for company: {}", jobs.size(), company);

        return jobs;
    }

    public List<job> getJobsBySalaryRange(double minSalary, double maxSalary) {

        log.info("Fetching jobs with salary between {} and {}", minSalary, maxSalary);

        List<job> jobs = repo.findBySalaryBetween(minSalary, maxSalary);

        log.info("Found {} jobs in salary range", jobs.size());

        return jobs;
    }

    public List<job> getActiveJobs() {

        log.info("Fetching active jobs");

        List<job> jobs = repo.findByIsActiveTrue();

        log.info("Found {} active jobs", jobs.size());

        return jobs;
    }

    public List<job> getJobsByJobType(String jobType) {

        log.info("Fetching jobs with job type: {}", jobType);

        List<job> jobs = repo.findByJobTypeIgnoreCase(jobType);

        log.info("Found {} jobs with job type: {}", jobs.size(), jobType);

        return jobs;
    }

    public List<job> getJobsByExperience(String experience) {

        log.info("Fetching jobs with experience: {}", experience);

        List<job> jobs = repo.findByExperienceIgnoreCase(experience);

        log.info("Found {} jobs with experience: {}", jobs.size(), experience);

        return jobs;
    }

}