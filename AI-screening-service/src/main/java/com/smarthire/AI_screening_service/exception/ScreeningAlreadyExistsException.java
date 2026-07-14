package com.smarthire.AI_screening_service.exception;

public class ScreeningAlreadyExistsException extends RuntimeException
{
    public ScreeningAlreadyExistsException(String message)
    {
        super(message);
    }
}