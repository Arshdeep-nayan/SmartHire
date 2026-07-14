package com.SmartHire.candidate_service.Exception;

public class CandidateAlreadyExistsException extends RuntimeException
{
    public CandidateAlreadyExistsException(String message)
    {
        super(message);
    }
}