package com.SmartHire.candidate_service;

import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Resume;
import com.SmartHire.candidate_service.Repository.ResumeRepository;
import com.SmartHire.candidate_service.Service.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository rrepo;

    @InjectMocks
    private ResumeService service;

    private Resume resume;

    @BeforeEach
    void setUp() {

        resume = new Resume();
        resume.setId("1");
        resume.setCandidateId(1);
        resume.setResumeText("Java Spring Boot Resume");
        resume.setUploadedAt(LocalDateTime.now());
    }

    @Test
    void testSaveResume_Success() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.empty());

        when(rrepo.save(any(Resume.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resume saved = service.saveResume(resume);

        assertNotNull(saved);
        assertEquals(1, saved.getCandidateId());
        assertEquals("Java Spring Boot Resume", saved.getResumeText());
        assertNotNull(saved.getUploadedAt());

        verify(rrepo).findByCandidateId(1);
        verify(rrepo).save(any(Resume.class));
    }

    @Test
    void testSaveResume_AlreadyExists() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.of(resume));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.saveResume(resume));

        assertTrue(exception.getMessage().contains("Resume already exists"));

        verify(rrepo).findByCandidateId(1);
        verify(rrepo, never()).save(any());
    }

    @Test
    void testGetResumeByCandidateId_Success() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.of(resume));

        Resume result = service.getResumeByCandidateId(1);

        assertNotNull(result);
        assertEquals(1, result.getCandidateId());

        verify(rrepo).findByCandidateId(1);
    }

    @Test
    void testGetResumeByCandidateId_NotFound() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.getResumeByCandidateId(1));

        assertEquals(
                "Resume not found for candidate id : 1",
                exception.getMessage());

        verify(rrepo).findByCandidateId(1);
    }

    @Test
    void testUpdateResume_Success() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.of(resume));

        when(rrepo.save(any(Resume.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resume updated =
                service.updateResume(1, "Updated Resume");

        assertEquals("Updated Resume", updated.getResumeText());
        assertNotNull(updated.getUploadedAt());

        verify(rrepo).findByCandidateId(1);
        verify(rrepo).save(any(Resume.class));
    }

    @Test
    void testUpdateResume_NotFound() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.updateResume(1, "Updated Resume"));

        assertEquals(
                "Resume not found for candidate id : 1",
                exception.getMessage());

        verify(rrepo).findByCandidateId(1);
        verify(rrepo, never()).save(any());
    }

    @Test
    void testDeleteResume_Success() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.of(resume));

        service.deleteResume(1);

        verify(rrepo).findByCandidateId(1);
        verify(rrepo).delete(resume);
    }

    @Test
    void testDeleteResume_NotFound() {

        when(rrepo.findByCandidateId(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.deleteResume(1));

        assertEquals(
                "Resume not found for candidate id : 1",
                exception.getMessage());

        verify(rrepo).findByCandidateId(1);
        verify(rrepo, never()).delete(any());
    }
}