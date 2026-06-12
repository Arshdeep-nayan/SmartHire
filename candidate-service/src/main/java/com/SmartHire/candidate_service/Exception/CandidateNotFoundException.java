package com.SmartHire.candidate_service.Exception;


public class CandidateNotFoundException extends RuntimeException
{
    public CandidateNotFoundException(String message)
    {
        super(message);
    }
}
