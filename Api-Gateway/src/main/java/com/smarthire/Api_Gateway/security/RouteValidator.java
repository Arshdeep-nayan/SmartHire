package com.smarthire.Api_Gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openEndpoints = List.of(

            // Authentication
            "/api/login",
            "/api/register",

            // Eureka
            "/eureka",

            // Actuator
            "/actuator",

            // Swagger UI
            "/swagger-ui",
            "/swagger-ui.html",

            // OpenAPI
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",

            // Aggregated OpenAPI Docs
            "/auth/v3/api-docs",
            "/jobs/v3/api-docs",
            "/candidate/v3/api-docs",
            "/screening/v3/api-docs",
            "/notification/v3/api-docs"
    );

    public Predicate<String> isSecured =
            uri -> openEndpoints
                    .stream()
                    .noneMatch(uri::startsWith);
}