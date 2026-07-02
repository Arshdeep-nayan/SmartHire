package com.smarthire.AI_screening_service.service;

import com.smarthire.AI_screening_service.model.ScreeningResult;
import com.smarthire.AI_screening_service.repository.ScreeningRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScreeningService {

    private final ScreeningRepository repo;
    private final ChatClient chatClient;

    public ScreeningService(ScreeningRepository repo, GoogleGenAiChatModel chatModel) {
        this.repo = repo;
        this.chatClient = ChatClient.create(chatModel);
    }

    public ScreeningResult screenCandidate(int candidateId, int jobId) {
        String resumeText = fetchResumeText(candidateId);
        String jobDescription = fetchJobDescription(jobId);

        String prompt = buildPrompt(resumeText, jobDescription);
        String geminiResponse = chatClient.prompt(prompt).call().content();
        int score = extractScore(geminiResponse);

        ScreeningResult result = new ScreeningResult();
        result.setCandidateId(candidateId);
        result.setJobId(jobId);
        result.setScore(score);
        result.setFeedback(geminiResponse);
        result.setScreenedAt(LocalDateTime.now());

        return repo.save(result);
    }

    private String fetchResumeText(int candidateId) {
        // TEMP for testing — Phase 6 will replace with real Feign call
        return "5 years experience in Java, Spring Boot, Microservices, REST APIs, MySQL, Docker.";
    }

    private String fetchJobDescription(int jobId) {
        // TEMP for testing — Phase 6 will replace with real Feign call
        return "Looking for a backend developer with 3+ years experience in Java and Spring Boot.";
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return "You are an expert recruiter. Given this candidate resume:\n" + resumeText +
                "\n\nAnd this job description:\n" + jobDescription +
                "\n\nProvide a match score out of 100 and brief feedback. " +
                "Respond in this exact format: SCORE: <number>\nFEEDBACK: <text>";
    }

    private int extractScore(String geminiResponse) {
        try {
            if (geminiResponse.contains("SCORE:")) {
                String scorePart = geminiResponse.split("SCORE:")[1].trim();
                return Integer.parseInt(scorePart.split("\\D")[0]);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}