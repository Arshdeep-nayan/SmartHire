package com.smarthire.AI_screening_service;

import com.smarthire.AI_screening_service.dto.CandidateDTO;
import com.smarthire.AI_screening_service.dto.JobDTO;
import com.smarthire.AI_screening_service.dto.ScreeningCompletedEvent;
import com.smarthire.AI_screening_service.exception.ScreeningAlreadyExistsException;
import com.smarthire.AI_screening_service.feign.CandidateFeignClient;
import com.smarthire.AI_screening_service.feign.JobFeignClient;
import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.repository.ScreeningRepository;
import com.smarthire.AI_screening_service.service.ScreeningEventProducer;
import com.smarthire.AI_screening_service.service.ScreeningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private ScreeningRepository repo;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @Mock
    private CandidateFeignClient candidateFeignClient;

    @Mock
    private JobFeignClient jobFeignClient;

    @Mock
    private ScreeningEventProducer screeningEventProducer;

    @InjectMocks
    private ScreeningService service;

    private CandidateDTO candidate;
    private JobDTO job;

    @BeforeEach
    void setUp() {

        candidate = new CandidateDTO();
        candidate.setId(1);
        candidate.setName("Arshdeep");
        candidate.setSkills("Java, Spring Boot");
        candidate.setExperience("Fresher");
        candidate.setCurrentLocation("Mohali");

        job = new JobDTO();
        job.setId(10);
        job.setTitle("Java Developer");
        job.setCompany("Google");
        job.setSkills("Java, Spring Boot");
        job.setExperience("Fresher");
        job.setDescription("Backend Development");
    }

    @Test
    void testScreenCandidate_Success() {

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.empty());

        when(candidateFeignClient.getCandidateById(1))
                .thenReturn(candidate);

        when(jobFeignClient.getJobById(10))
                .thenReturn(job);

        when(chatClient.prompt(anyString()))
                .thenReturn(requestSpec);

        when(requestSpec.call())
                .thenReturn(responseSpec);

        when(responseSpec.content())
                .thenReturn("SCORE: 92\nFEEDBACK: Excellent Match");

        when(repo.save(any(ScreeningResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScreeningResult result =
                service.screenCandidate(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getCandidateId());
        assertEquals(10, result.getJobId());
        assertEquals(92, result.getScore());
        assertEquals(
                "SCORE: 92\nFEEDBACK: Excellent Match",
                result.getFeedback());

        verify(candidateFeignClient)
                .getCandidateById(1);

        verify(jobFeignClient)
                .getJobById(10);

        verify(chatClient)
                .prompt(anyString());

        verify(screeningEventProducer)
                .publishScreeningCompletedEvent(
                        any(ScreeningCompletedEvent.class));

        ArgumentCaptor<ScreeningResult> captor =
                ArgumentCaptor.forClass(ScreeningResult.class);

        verify(repo).save(captor.capture());

        assertEquals(92, captor.getValue().getScore());
    }

    @Test
    void testScreenCandidate_AlreadyExists() {

        ScreeningResult existing =
                new ScreeningResult();

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.of(existing));

        ScreeningAlreadyExistsException exception =
                assertThrows(
                        ScreeningAlreadyExistsException.class,
                        () -> service.screenCandidate(1, 10));

        assertEquals(
                "Screening already exists for candidate id 1 and job id 10",
                exception.getMessage());

        verify(candidateFeignClient, never())
                .getCandidateById(anyInt());

        verify(jobFeignClient, never())
                .getJobById(anyInt());

        verify(repo, never())
                .save(any());
    }
    @Test
    void testGetAllScreenings() {

        ScreeningResult screening = new ScreeningResult();
        screening.setId("1");

        when(repo.findAll())
                .thenReturn(java.util.List.of(screening));

        java.util.List<ScreeningResult> result =
                service.getAllScreenings();

        assertEquals(1, result.size());
        assertEquals(screening, result.get(0));

        verify(repo).findAll();
    }

    @Test
    void testGetScreeningById_Success() {

        ScreeningResult screening = new ScreeningResult();
        screening.setId("1");

        when(repo.findById("1"))
                .thenReturn(Optional.of(screening));

        ScreeningResult result =
                service.getScreeningById("1");

        assertEquals("1", result.getId());

        verify(repo).findById("1");
    }

    @Test
    void testGetScreeningById_NotFound() {

        when(repo.findById("1"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getScreeningById("1"));

        assertEquals(
                "Screening not found with id : 1",
                exception.getMessage());

        verify(repo).findById("1");
    }

    @Test
    void testGetScreeningByCandidateId_Success() {

        ScreeningResult screening = new ScreeningResult();
        screening.setCandidateId(1);

        when(repo.findByCandidateId(1))
                .thenReturn(java.util.List.of(screening));

        java.util.List<ScreeningResult> result =
                service.getScreeningByCandidateId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCandidateId());

        verify(repo).findByCandidateId(1);
    }

    @Test
    void testGetScreeningByCandidateId_NotFound() {

        when(repo.findByCandidateId(1))
                .thenReturn(java.util.Collections.emptyList());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getScreeningByCandidateId(1));

        assertEquals(
                "No screenings found for candidate id : 1",
                exception.getMessage());

        verify(repo).findByCandidateId(1);
    }

    @Test
    void testGetScreeningByJobId_Success() {

        ScreeningResult screening = new ScreeningResult();
        screening.setJobId(10);

        when(repo.findByJobIdOrderByScoreDesc(10))
                .thenReturn(java.util.List.of(screening));

        java.util.List<ScreeningResult> result =
                service.getScreeningByJobId(10);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getJobId());

        verify(repo).findByJobIdOrderByScoreDesc(10);
    }

    @Test
    void testGetScreeningByJobId_NotFound() {

        when(repo.findByJobIdOrderByScoreDesc(10))
                .thenReturn(java.util.Collections.emptyList());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getScreeningByJobId(10));

        assertEquals(
                "No screenings found for job id : 10",
                exception.getMessage());

        verify(repo).findByJobIdOrderByScoreDesc(10);
    }

    @Test
    void testGetScreeningByCandidateAndJob_Success() {

        ScreeningResult screening = new ScreeningResult();
        screening.setCandidateId(1);
        screening.setJobId(10);

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.of(screening));

        ScreeningResult result =
                service.getScreeningByCandidateAndJob(1, 10);

        assertEquals(1, result.getCandidateId());
        assertEquals(10, result.getJobId());

        verify(repo).findByCandidateIdAndJobId(1, 10);
    }

    @Test
    void testGetScreeningByCandidateAndJob_NotFound() {

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.getScreeningByCandidateAndJob(1, 10));

        assertEquals(
                "No screening found for candidate id : 1 and job id : 10",
                exception.getMessage());

        verify(repo).findByCandidateIdAndJobId(1, 10);
    }
    @Test
    void testDeleteScreening_Success() {

        when(repo.existsById("1"))
                .thenReturn(true);

        service.deleteScreening("1");

        verify(repo).existsById("1");
        verify(repo).deleteById("1");
    }

    @Test
    void testDeleteScreening_NotFound() {

        when(repo.existsById("1"))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.deleteScreening("1"));

        assertEquals(
                "Screening not found with id : 1",
                exception.getMessage());

        verify(repo).existsById("1");
        verify(repo, never()).deleteById(anyString());
    }

    @Test
    void testScreenCandidate_InvalidGeminiResponse() {

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.empty());

        when(candidateFeignClient.getCandidateById(1))
                .thenReturn(candidate);

        when(jobFeignClient.getJobById(10))
                .thenReturn(job);

        when(chatClient.prompt(anyString()))
                .thenReturn(requestSpec);

        when(requestSpec.call())
                .thenReturn(responseSpec);

        when(responseSpec.content())
                .thenReturn("INVALID RESPONSE");

        when(repo.save(any(ScreeningResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScreeningResult result =
                service.screenCandidate(1, 10);

        assertEquals(0, result.getScore());

        verify(screeningEventProducer)
                .publishScreeningCompletedEvent(any());
    }

    @Test
    void testScreenCandidate_NonNumericScore() {

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.empty());

        when(candidateFeignClient.getCandidateById(1))
                .thenReturn(candidate);

        when(jobFeignClient.getJobById(10))
                .thenReturn(job);

        when(chatClient.prompt(anyString()))
                .thenReturn(requestSpec);

        when(requestSpec.call())
                .thenReturn(responseSpec);

        when(responseSpec.content())
                .thenReturn("SCORE: ABC\nFEEDBACK: Good");

        when(repo.save(any(ScreeningResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScreeningResult result =
                service.screenCandidate(1, 10);

        assertEquals(0, result.getScore());

        verify(repo).save(any(ScreeningResult.class));
    }

    @Test
    void testScreenCandidate_PerfectScore() {

        when(repo.findByCandidateIdAndJobId(1, 10))
                .thenReturn(Optional.empty());

        when(candidateFeignClient.getCandidateById(1))
                .thenReturn(candidate);

        when(jobFeignClient.getJobById(10))
                .thenReturn(job);

        when(chatClient.prompt(anyString()))
                .thenReturn(requestSpec);

        when(requestSpec.call())
                .thenReturn(responseSpec);

        when(responseSpec.content())
                .thenReturn("SCORE: 100\nFEEDBACK: Perfect Match");

        when(repo.save(any(ScreeningResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScreeningResult result =
                service.screenCandidate(1, 10);

        assertEquals(100, result.getScore());

        verify(screeningEventProducer)
                .publishScreeningCompletedEvent(any());
    }
}