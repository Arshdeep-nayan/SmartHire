package com.smarthire.AI_screening_service.service;

import com.smarthire.AI_screening_service.dto.CandidateDTO;
import com.smarthire.AI_screening_service.dto.JobDTO;
import com.smarthire.AI_screening_service.dto.ScreeningCompletedEvent;
import com.smarthire.AI_screening_service.exception.ScreeningAlreadyExistsException;
import com.smarthire.AI_screening_service.exception.ScreeningNotFoundException;
import com.smarthire.AI_screening_service.feign.CandidateFeignClient;
import com.smarthire.AI_screening_service.feign.JobFeignClient;
import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.repository.ScreeningRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ScreeningService {

    private final ScreeningRepository repo;
    private final ChatClient chatClient;
    private final CandidateFeignClient candidateFeignClient;
    private final JobFeignClient jobFeignClient;
    private final ScreeningEventProducer screeningEventProducer;

    public ScreeningService(
            ScreeningRepository repo,
            GoogleGenAiChatModel chatModel,
            CandidateFeignClient candidateFeignClient,
            JobFeignClient jobFeignClient,
            ScreeningEventProducer screeningEventProducer) {

        this.repo = repo;
        this.chatClient = ChatClient.create(chatModel);
        this.candidateFeignClient = candidateFeignClient;
        this.jobFeignClient = jobFeignClient;
        this.screeningEventProducer = screeningEventProducer;
    }

    public ScreeningResult screenCandidate(
            int candidateId,
            int jobId) {

        log.info("Starting AI screening for Candidate {} and Job {}", candidateId, jobId);

        repo.findByCandidateIdAndJobId(candidateId, jobId)
                .ifPresent(existing -> {

                    log.warn(
                            "Screening already exists for candidate {} and job {}",
                            candidateId,
                            jobId);

                    throw new ScreeningAlreadyExistsException(
                            "Screening already exists for candidate id "
                                    + candidateId
                                    + " and job id "
                                    + jobId);
                });

        CandidateDTO candidate =
                candidateFeignClient.getCandidateById(candidateId);

        JobDTO job =
                jobFeignClient.getJobById(jobId);

        log.debug("Candidate: {}", candidate);
        log.debug("Job: {}", job);

        String resumeText = buildResumeText(candidate);
        String jobDescription = buildJobDescription(job);

        String prompt = buildPrompt(
                resumeText,
                jobDescription);

        log.debug("Prompt sent to Gemini:\n{}", prompt);

        String geminiResponse =
                chatClient.prompt(prompt)
                        .call()
                        .content();

        log.debug("Gemini Response:\n{}", geminiResponse);

        int score = extractScore(geminiResponse);

        if (score == 0) {
            log.warn("Unable to extract score from Gemini response.");
        }

        log.info("Screening completed with score {}", score);

        ScreeningCompletedEvent event =
                new ScreeningCompletedEvent(
                        candidateId,
                        jobId,
                        score
                );

        screeningEventProducer.publishScreeningCompletedEvent(event);

        ScreeningResult result =
                new ScreeningResult(
                        null,
                        candidateId,
                        jobId,
                        score,
                        geminiResponse,
                        LocalDateTime.now()
                );

        return repo.save(result);
    }

    private String buildResumeText(
            CandidateDTO candidate) {

        return "Name : " + candidate.getName()
                + "\nSkills : " + candidate.getSkills()
                + "\nExperience : " + candidate.getExperience()
                + "\nLocation : " + candidate.getCurrentLocation();
    }

    private String buildJobDescription(
            JobDTO job) {

        return "Title : " + job.getTitle()
                + "\nCompany : " + job.getCompany()
                + "\nRequired Skills : " + job.getSkills()
                + "\nExperience : " + job.getExperience()
                + "\nDescription : " + job.getDescription();
    }

    private String buildPrompt(
            String resumeText,
            String jobDescription) {

        return "You are an expert recruiter. Given this candidate resume:\n"
                + resumeText
                + "\n\nAnd this job description:\n"
                + jobDescription
                + "\n\nProvide a match score out of 100 and brief feedback. "
                + "Respond in this exact format: "
                + "SCORE: <number>\n"
                + "FEEDBACK: <text>";
    }

    private int extractScore(String geminiResponse) {

        try {
            if (geminiResponse.contains("SCORE:")) {

                String scorePart =
                        geminiResponse
                                .split("SCORE:")[1]
                                .trim();

                return Integer.parseInt(
                        scorePart.split("\\D")[0]);
            }
        }
        catch (Exception e) {
            log.error("Error while extracting score from Gemini response.", e);
            return 0;
        }

        return 0;
    }

    public List<ScreeningResult> getAllScreenings() {
        return repo.findAll();
    }

    public ScreeningResult getScreeningById(String id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new ScreeningNotFoundException(
                                "Screening not found with id : " + id));
    }

    public List<ScreeningResult> getScreeningByCandidateId(
            int candidateId) {

        List<ScreeningResult> screenings =
                repo.findByCandidateId(candidateId);

        if (screenings.isEmpty()) {
            throw new ScreeningNotFoundException(
                    "No screenings found for candidate id : "
                            + candidateId);
        }

        return screenings;
    }

    public List<ScreeningResult> getScreeningByJobId(
            int jobId) {

        List<ScreeningResult> screenings =
                repo.findByJobIdOrderByScoreDesc(jobId);

        if (screenings.isEmpty()) {
            throw new ScreeningNotFoundException(
                    "No screenings found for job id : "
                            + jobId);
        }

        return screenings;
    }

    public ScreeningResult getScreeningByCandidateAndJob(
            int candidateId,
            int jobId) {

        return repo.findByCandidateIdAndJobId(
                        candidateId,
                        jobId)
                .orElseThrow(() ->
                        new ScreeningNotFoundException(
                                "No screening found for candidate id : "
                                        + candidateId
                                        + " and job id : "
                                        + jobId));
    }

    public void deleteScreening(String id) {

        if (!repo.existsById(id)) {
            throw new ScreeningNotFoundException(
                    "Screening not found with id : " + id);
        }

        repo.deleteById(id);
    }
}