package com.smarthire.AI_screening_service.service;

import com.smarthire.AI_screening_service.exception.ScreeningNotFoundException;
import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.repository.ScreeningRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScreeningService {

    private final ScreeningRepository repo;
    private final ChatClient chatClient;

    public ScreeningService(ScreeningRepository repo,
                            GoogleGenAiChatModel chatModel) {
        this.repo = repo;
        this.chatClient = ChatClient.create(chatModel);
    }

    public ScreeningResult screenCandidate(int candidateId, int jobId) {

        String resumeText = fetchResumeText(candidateId);
        String jobDescription = fetchJobDescription(jobId);

        String prompt = buildPrompt(resumeText, jobDescription);
        String geminiResponse =
                chatClient.prompt(prompt).call().content();

        int score = extractScore(geminiResponse);

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


    private String fetchResumeText(int candidateId) {
        return "5 years experience in Java, Spring Boot, Microservices, REST APIs, MySQL, Docker.";
    }

    private String fetchJobDescription(int jobId) {
        return "Looking for a backend developer with 3+ years experience in Java and Spring Boot.";
    }

    private String buildPrompt(String resumeText,
                               String jobDescription) {

        return "You are an expert recruiter. Given this candidate resume:\n"
                + resumeText
                + "\n\nAnd this job description:\n"
                + jobDescription
                + "\n\nProvide a match score out of 100 and brief feedback. "
                + "Respond in this exact format: SCORE: <number>\nFEEDBACK: <text>";
    }

    private int extractScore(String geminiResponse) {
        try {
            if (geminiResponse.contains("SCORE:")) {
                String scorePart =
                        geminiResponse.split("SCORE:")[1].trim();

                return Integer.parseInt(
                        scorePart.split("\\D")[0]);
            }
        } catch (Exception e) {
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
                repo.findByJobId(jobId);

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