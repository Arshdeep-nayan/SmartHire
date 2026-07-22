package com.smarthire.Api_Gateway;

import com.smarthire.Api_Gateway.security.RouteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteValidatorTest {

    private RouteValidator routeValidator;

    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
    }

    @Test
    void testLoginEndpointIsPublic() {
        assertFalse(routeValidator.isSecured.test("/api/login"));
    }

    @Test
    void testRegisterEndpointIsPublic() {
        assertFalse(routeValidator.isSecured.test("/api/register"));
    }

    @Test
    void testSwaggerUiIsPublic() {
        assertFalse(routeValidator.isSecured.test("/swagger-ui/index.html"));
    }

    @Test
    void testSwaggerHtmlIsPublic() {
        assertFalse(routeValidator.isSecured.test("/swagger-ui.html"));
    }

    @Test
    void testApiDocsIsPublic() {
        assertFalse(routeValidator.isSecured.test("/v3/api-docs"));
    }

    @Test
    void testSwaggerConfigIsPublic() {
        assertFalse(routeValidator.isSecured.test("/v3/api-docs/swagger-config"));
    }

    @Test
    void testAuthDocsArePublic() {
        assertFalse(routeValidator.isSecured.test("/auth/v3/api-docs"));
    }

    @Test
    void testJobDocsArePublic() {
        assertFalse(routeValidator.isSecured.test("/jobs/v3/api-docs"));
    }

    @Test
    void testCandidateDocsArePublic() {
        assertFalse(routeValidator.isSecured.test("/candidate/v3/api-docs"));
    }

    @Test
    void testScreeningDocsArePublic() {
        assertFalse(routeValidator.isSecured.test("/screening/v3/api-docs"));
    }

    @Test
    void testNotificationDocsArePublic() {
        assertFalse(routeValidator.isSecured.test("/notification/v3/api-docs"));
    }

    @Test
    void testEurekaEndpointIsPublic() {
        assertFalse(routeValidator.isSecured.test("/eureka/apps"));
    }

    @Test
    void testActuatorEndpointIsPublic() {
        assertFalse(routeValidator.isSecured.test("/actuator/health"));
    }

    @Test
    void testJobsEndpointIsSecured() {
        assertTrue(routeValidator.isSecured.test("/jobs/jobs/all"));
    }

    @Test
    void testCandidateEndpointIsSecured() {
        assertTrue(routeValidator.isSecured.test("/api/candidate/1"));
    }

    @Test
    void testScreeningEndpointIsSecured() {
        assertTrue(routeValidator.isSecured.test("/api/screening/all"));
    }

    @Test
    void testNotificationEndpointIsSecured() {
        assertTrue(routeValidator.isSecured.test("/api/notification/all"));
    }

    @Test
    void testUnknownEndpointIsSecured() {
        assertTrue(routeValidator.isSecured.test("/some/random/path"));
    }
}