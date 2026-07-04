package com.smarthire.Api_Gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openEndpoints = List.of(
            "/api/login",
            "/api/register",
            "/eureka"
    );

    public Predicate<String> isSecured =
            uri -> openEndpoints
                    .stream()
                    .noneMatch(uri::startsWith);
}