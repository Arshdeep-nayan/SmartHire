package com.smarthire.job_service.exception;

public class JobNotFoundException extends RuntimeException
{
    public JobNotFoundException(int id)
    {
        super("Job not found with id: " + id);
    }
}
