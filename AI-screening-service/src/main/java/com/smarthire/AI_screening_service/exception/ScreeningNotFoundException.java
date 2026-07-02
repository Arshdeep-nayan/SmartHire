package com.smarthire.AI_screening_service.exception;

public class ScreeningNotFoundException extends RuntimeException
{
    public ScreeningNotFoundException(String message){
        super(message);
    }
}
