package com.smarthire.job_service;

import com.smarthire.job_service.model.job;
import com.smarthire.job_service.repository.JobRepository;
import com.smarthire.job_service.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository repo;

    @InjectMocks
    private JobService service;

    private job sampleJob;

    @BeforeEach
    void setUp() {
        sampleJob = new job();
        sampleJob.setId(1);
        sampleJob.setTitle("Java Developer");
        sampleJob.setCompany("Google");
        sampleJob.setLocation("Bangalore");
        sampleJob.setDescription("Backend Development");
        sampleJob.setSalary(1200000);
        sampleJob.setJobType("Full Time");
        sampleJob.setExperience("2 Years");
        sampleJob.setSkills("Java, Spring Boot");
        sampleJob.setActive(true);
    }

    @Test
    void testGetAllJobs() {

        when(repo.findAll()).thenReturn(List.of(sampleJob));

        List<job> jobs = service.getAllJobs();

        assertEquals(1, jobs.size());
        assertEquals(sampleJob, jobs.get(0));

        verify(repo).findAll();
    }

    @Test
    void testGetJobById_Success() {

        when(repo.findById(1))
                .thenReturn(Optional.of(sampleJob));

        job result = service.getJobById(1);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());

        verify(repo).findById(1);
    }

    @Test
    void testGetJobById_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getJobById(1));

        assertEquals("Job not found", exception.getMessage());

        verify(repo).findById(1);
    }

    @Test
    void testCreateJob() {

        when(repo.save(sampleJob))
                .thenReturn(sampleJob);

        job savedJob = service.createJob(sampleJob);

        assertNotNull(savedJob);
        assertEquals(sampleJob, savedJob);

        verify(repo).save(sampleJob);
    }

    @Test
    void testUpdateJob_Success() {

        job updatedJob = new job();

        updatedJob.setTitle("Senior Java Developer");
        updatedJob.setCompany("Microsoft");
        updatedJob.setLocation("Hyderabad");
        updatedJob.setDescription("Spring Boot Microservices");
        updatedJob.setSalary(1800000);
        updatedJob.setJobType("Full Time");
        updatedJob.setExperience("5 Years");
        updatedJob.setSkills("Java, Spring Boot, Kafka");
        updatedJob.setActive(false);

        when(repo.findById(1))
                .thenReturn(Optional.of(sampleJob));

        when(repo.save(any(job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        job result = service.updateJob(1, updatedJob);

        assertEquals("Senior Java Developer", result.getTitle());
        assertEquals("Microsoft", result.getCompany());
        assertEquals("Hyderabad", result.getLocation());
        assertEquals("Spring Boot Microservices", result.getDescription());
        assertEquals(1800000, result.getSalary());
        assertEquals("Full Time", result.getJobType());
        assertEquals("5 Years", result.getExperience());
        assertEquals("Java, Spring Boot, Kafka", result.getSkills());
        assertFalse(result.isActive());

        verify(repo).findById(1);

        ArgumentCaptor<job> captor =
                ArgumentCaptor.forClass(job.class);

        verify(repo).save(captor.capture());

        assertEquals("Senior Java Developer",
                captor.getValue().getTitle());
    }

    @Test
    void testUpdateJob_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.updateJob(1, sampleJob));

        assertEquals("Job not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testDeleteJob_Success() {

        when(repo.findById(1))
                .thenReturn(Optional.of(sampleJob));

        service.deleteJob(1);

        verify(repo).findById(1);
        verify(repo).delete(sampleJob);
    }

    @Test
    void testDeleteJob_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.deleteJob(1));

        assertEquals("Job not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).delete(any());
    }
    @Test
    void testActivateJob_Success() {

        sampleJob.setActive(false);

        when(repo.findById(1))
                .thenReturn(Optional.of(sampleJob));

        when(repo.save(any(job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        job result = service.activateJob(1);

        assertTrue(result.isActive());

        verify(repo).findById(1);
        verify(repo).save(sampleJob);
    }

    @Test
    void testActivateJob_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.activateJob(1));

        assertEquals("Job not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testDeactivateJob_Success() {

        sampleJob.setActive(true);

        when(repo.findById(1))
                .thenReturn(Optional.of(sampleJob));

        when(repo.save(any(job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        job result = service.deactivateJob(1);

        assertFalse(result.isActive());

        verify(repo).findById(1);
        verify(repo).save(sampleJob);
    }

    @Test
    void testDeactivateJob_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.deactivateJob(1));

        assertEquals("Job not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testGetJobsPaginated() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<job> page =
                new PageImpl<>(List.of(sampleJob), pageable, 1);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<job> result =
                service.getJobsPaginated(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(sampleJob, result.getContent().get(0));

        verify(repo).findAll(any(Pageable.class));
    }

    @Test
    void testGetJobsSortedBySalary() {

        when(repo.findAll(any(Sort.class)))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsSortedBySalary();

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findAll(any(Sort.class));
    }

    @Test
    void testGetJobsSortedByPostedAt() {

        when(repo.findAll(any(Sort.class)))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsSortedByPostedAt();

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findAll(any(Sort.class));
    }
    @Test
    void testSearchJobs() {

        when(repo.searchjobs("Java"))
                .thenReturn(List.of(sampleJob));

        List<job> result = service.searchJobs("Java");

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).searchjobs("Java");
    }

    @Test
    void testGetJobsByLocation() {

        when(repo.findByLocationIgnoreCase("Bangalore"))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsByLocation("Bangalore");

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findByLocationIgnoreCase("Bangalore");
    }

    @Test
    void testGetJobsByCompany() {

        when(repo.findByCompanyIgnoreCase("Google"))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsByCompany("Google");

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findByCompanyIgnoreCase("Google");
    }

    @Test
    void testGetJobsBySalaryRange() {

        when(repo.findBySalaryBetween(1000000, 1500000))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsBySalaryRange(1000000, 1500000);

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findBySalaryBetween(1000000, 1500000);
    }

    @Test
    void testGetActiveJobs() {

        when(repo.findByIsActiveTrue())
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getActiveJobs();

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findByIsActiveTrue();
    }

    @Test
    void testGetJobsByJobType() {

        when(repo.findByJobTypeIgnoreCase("Full Time"))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsByJobType("Full Time");

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findByJobTypeIgnoreCase("Full Time");
    }

    @Test
    void testGetJobsByExperience() {

        when(repo.findByExperienceIgnoreCase("2 Years"))
                .thenReturn(List.of(sampleJob));

        List<job> result =
                service.getJobsByExperience("2 Years");

        assertEquals(1, result.size());
        assertEquals(sampleJob, result.get(0));

        verify(repo).findByExperienceIgnoreCase("2 Years");
    }
}
