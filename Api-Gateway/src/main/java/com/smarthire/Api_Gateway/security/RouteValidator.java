package com.smarthire.Api_Gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
public class RouteValidator {

    public static final List<String> openEndpoints = List.of(

            "/api/login",
            "/api/register",

            "/eureka",

            "/actuator",

            "/swagger-ui",
            "/swagger-ui.html",

            "/v3/api-docs",
            "/v3/api-docs/swagger-config",

            "/auth/v3/api-docs",
            "/jobs/v3/api-docs",
            "/candidate/v3/api-docs",
            "/screening/v3/api-docs",
            "/notification/v3/api-docs"
    );

    public Predicate<String> isSecured = uri -> {

        boolean secured = openEndpoints
                .stream()
                .noneMatch(uri::startsWith);

        log.debug(
                "Route [{}] is {}",
                uri,
                secured ? "SECURED" : "PUBLIC");

        return secured;
    };
}