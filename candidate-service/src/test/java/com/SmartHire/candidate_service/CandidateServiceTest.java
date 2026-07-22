package com.SmartHire.candidate_service;

import com.SmartHire.candidate_service.Exception.CandidateAlreadyExistsException;
import com.SmartHire.candidate_service.Exception.CandidateNotFoundException;
import com.SmartHire.candidate_service.Model.Candidate;
import com.SmartHire.candidate_service.Repository.CandidateRepository;
import com.SmartHire.candidate_service.Service.CandidateEventProducer;
import com.SmartHire.candidate_service.Service.CandidateService;
import com.SmartHire.candidate_service.dto.CandidateAppliedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository repo;

    @Mock
    private CandidateEventProducer producer;

    @InjectMocks
    private CandidateService service;

    private Candidate candidate;

    @BeforeEach
    void setUp() {

        candidate = new Candidate();

        candidate.setId(1);
        candidate.setName("Arshdeep Kumar");
        candidate.setEmail("arshdeep@gmail.com");
        candidate.setPhone("9876543210");
        candidate.setSkills("Java, Spring Boot");
        candidate.setExperience("Fresher");
        candidate.setCurrentLocation("Mohali");
        candidate.setStatus(Candidate.CandidateStatus.ACTIVE);
    }

    @Test
    void testGetAllCandidates() {

        when(repo.findAll())
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getAllCandidates();

        assertEquals(1, result.size());
        assertEquals(candidate, result.get(0));

        verify(repo).findAll();
    }

    @Test
    void testGetCandidateById_Success() {

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        Candidate result =
                service.getCandidateById(1);

        assertNotNull(result);
        assertEquals("Arshdeep Kumar", result.getName());

        verify(repo).findById(1);
    }

    @Test
    void testGetCandidateById_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.getCandidateById(1));

        assertEquals(
                "Candidate not found",
                exception.getMessage());

        verify(repo).findById(1);
    }

    @Test
    void testRegisterCandidate_Success() {

        when(repo.findByEmail(candidate.getEmail()))
                .thenReturn(Optional.empty());

        when(repo.save(candidate))
                .thenReturn(candidate);

        Candidate saved =
                service.registerCandidate(candidate);

        assertNotNull(saved);
        assertEquals(candidate, saved);

        verify(repo).findByEmail(candidate.getEmail());
        verify(repo).save(candidate);
    }

    @Test
    void testRegisterCandidate_AlreadyExists() {

        when(repo.findByEmail(candidate.getEmail()))
                .thenReturn(Optional.of(candidate));

        CandidateAlreadyExistsException exception =
                assertThrows(
                        CandidateAlreadyExistsException.class,
                        () -> service.registerCandidate(candidate));

        assertEquals(
                "Candidate already exists with email: " + candidate.getEmail(),
                exception.getMessage());

        verify(repo).findByEmail(candidate.getEmail());
        verify(repo, never()).save(any());
    }
    @Test
    void testUpdateCandidate_Success() {

        Candidate updated = new Candidate();

        updated.setName("Rahul Sharma");
        updated.setEmail("rahul@gmail.com");
        updated.setPhone("9999999999");
        updated.setSkills("Spring Boot, Kafka");
        updated.setCurrentLocation("Bangalore");
        updated.setExperience("2 Years");

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        when(repo.save(any(Candidate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result =
                service.updateCandidate(1, updated);

        assertEquals("Rahul Sharma", result.getName());
        assertEquals("rahul@gmail.com", result.getEmail());
        assertEquals("9999999999", result.getPhone());
        assertEquals("Spring Boot, Kafka", result.getSkills());
        assertEquals("Bangalore", result.getCurrentLocation());
        assertEquals("2 Years", result.getExperience());

        verify(repo).findById(1);

        ArgumentCaptor<Candidate> captor =
                ArgumentCaptor.forClass(Candidate.class);

        verify(repo).save(captor.capture());

        assertEquals("Rahul Sharma",
                captor.getValue().getName());
    }

    @Test
    void testUpdateCandidate_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.updateCandidate(1, candidate));

        assertEquals(
                "Candidate you are trying to update doesn't exist",
                exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testDeleteCandidate_Success() {

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        service.deleteCandidate(1);

        verify(repo).findById(1);
        verify(repo).deleteById(1);
    }

    @Test
    void testDeleteCandidate_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.deleteCandidate(1));

        assertEquals(
                "Candidate you are trying to delete doesn't exist",
                exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).deleteById(anyInt());
    }

    @Test
    void testSearchByKeyword() {

        when(repo.searchByKeyword("Java"))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.searchByKeyword("Java");

        assertEquals(1, result.size());
        assertEquals(candidate, result.get(0));

        verify(repo).searchByKeyword("Java");
    }

    @Test
    void testGetCandidatesBySkill() {

        when(repo.findBySkillsContainingIgnoreCase("Java"))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getCandidatesBySkill("Java");

        assertEquals(1, result.size());

        verify(repo).findBySkillsContainingIgnoreCase("Java");
    }

    @Test
    void testGetCandidatesByLocation() {

        when(repo.findByCurrentLocationIgnoreCase("Mohali"))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getCandidatesByLocation("Mohali");

        assertEquals(1, result.size());

        verify(repo).findByCurrentLocationIgnoreCase("Mohali");
    }

    @Test
    void testGetCandidatesByExperience() {

        when(repo.findByExperienceIgnoreCase("Fresher"))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getCandidatesByExperience("Fresher");

        assertEquals(1, result.size());

        verify(repo).findByExperienceIgnoreCase("Fresher");
    }

    @Test
    void testGetCandidatesByStatus() {

        when(repo.findByStatus(Candidate.CandidateStatus.ACTIVE))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getCandidatesByStatus(Candidate.CandidateStatus.ACTIVE);

        assertEquals(1, result.size());

        verify(repo).findByStatus(Candidate.CandidateStatus.ACTIVE);
    }
    @Test
    void testActivateCandidate_Success() {

        candidate.setStatus(Candidate.CandidateStatus.INACTIVE);

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        when(repo.save(any(Candidate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result = service.activateCandidate(1);

        assertEquals(Candidate.CandidateStatus.ACTIVE, result.getStatus());

        verify(repo).findById(1);
        verify(repo).save(candidate);
    }

    @Test
    void testActivateCandidate_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.activateCandidate(1));

        assertEquals("Candidate not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testDeactivateCandidate_Success() {

        candidate.setStatus(Candidate.CandidateStatus.ACTIVE);

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        when(repo.save(any(Candidate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result = service.deactivateCandidate(1);

        assertEquals(Candidate.CandidateStatus.INACTIVE, result.getStatus());

        verify(repo).findById(1);
        verify(repo).save(candidate);
    }

    @Test
    void testDeactivateCandidate_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.deactivateCandidate(1));

        assertEquals("Candidate not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testMarkCandidateAsHired_Success() {

        candidate.setStatus(Candidate.CandidateStatus.ACTIVE);

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        when(repo.save(any(Candidate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result = service.markCandidateAsHired(1);

        assertEquals(Candidate.CandidateStatus.HIRED, result.getStatus());

        verify(repo).findById(1);
        verify(repo).save(candidate);
    }

    @Test
    void testMarkCandidateAsHired_NotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.markCandidateAsHired(1));

        assertEquals("Candidate not found", exception.getMessage());

        verify(repo).findById(1);
        verify(repo, never()).save(any());
    }

    @Test
    void testGetCandidatesPaginated() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Candidate> page =
                new PageImpl<>(List.of(candidate), pageable, 1);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<Candidate> result =
                service.getCandidatesPaginated(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(candidate, result.getContent().get(0));

        verify(repo).findAll(any(Pageable.class));
    }

    @Test
    void testGetCandidatesSortedByDate() {

        when(repo.findAll(any(Sort.class)))
                .thenReturn(List.of(candidate));

        List<Candidate> result =
                service.getCandidatesSortedByDate();

        assertEquals(1, result.size());
        assertEquals(candidate, result.get(0));

        verify(repo).findAll(any(Sort.class));
    }

    @Test
    void testApplyForJob_Success() {

        when(repo.findById(1))
                .thenReturn(Optional.of(candidate));

        service.applyForJob(1, 10);

        ArgumentCaptor<CandidateAppliedEvent> captor =
                ArgumentCaptor.forClass(CandidateAppliedEvent.class);

        verify(producer).publishCandidateAppliedEvent(captor.capture());

        assertEquals(1, captor.getValue().getCandidateId());
        assertEquals(10, captor.getValue().getJobId());
    }

    @Test
    void testApplyForJob_CandidateNotFound() {

        when(repo.findById(1))
                .thenReturn(Optional.empty());

        CandidateNotFoundException exception =
                assertThrows(
                        CandidateNotFoundException.class,
                        () -> service.applyForJob(1, 10));

        assertEquals("Candidate not found", exception.getMessage());

        verify(repo).findById(1);
        verify(producer, never()).publishCandidateAppliedEvent(any());
    }
}

