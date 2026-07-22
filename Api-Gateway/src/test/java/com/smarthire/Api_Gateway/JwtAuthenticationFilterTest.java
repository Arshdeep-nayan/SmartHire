package com.smarthire.Api_Gateway;

import com.smarthire.Api_Gateway.security.JwtAuthenticationFilter;
import com.smarthire.Api_Gateway.security.JwtUtil;
import com.smarthire.Api_Gateway.security.RouteValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    private RouteValidator routeValidator;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {

        SecurityContextHolder.clearContext();

        routeValidator = new RouteValidator();
        filter =
                new JwtAuthenticationFilter(
                        jwtUtil,
                        routeValidator);

        responseWriter = new StringWriter();

        lenient()
                .when(response.getWriter())
                .thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testPublicEndpoint() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/login");

        when(request.getMethod())
                .thenReturn("POST");

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);

        verifyNoInteractions(jwtUtil);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }
    @Test
    void testMissingAuthorizationHeader() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Missing Authorization Header",
                responseWriter.toString());
    }

    @Test
    void testAuthorizationHeaderWithoutBearer() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Missing Authorization Header",
                responseWriter.toString());
    }

    @Test
    void testInvalidToken() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer invalidToken");

        when(jwtUtil.extractUsername("invalidToken"))
                .thenReturn("admin@test.com");

        when(jwtUtil.extractRole("invalidToken"))
                .thenReturn("ADMIN");

        when(jwtUtil.extractUserId("invalidToken"))
                .thenReturn(1);

        when(jwtUtil.validateToken("invalidToken"))
                .thenReturn(false);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Invalid or Expired JWT Token",
                responseWriter.toString());
    }

    @Test
    void testJwtParsingException() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtUtil.extractUsername("token"))
                .thenThrow(new RuntimeException("Invalid JWT"));

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Invalid JWT Token",
                responseWriter.toString());
    }
    @Test
    void testAdminAuthenticationSuccess() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer validToken");

        when(jwtUtil.extractUsername("validToken"))
                .thenReturn("admin@test.com");

        when(jwtUtil.extractRole("validToken"))
                .thenReturn("ADMIN");

        when(jwtUtil.extractUserId("validToken"))
                .thenReturn(1);

        when(jwtUtil.validateToken("validToken"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());

        assertEquals(
                "admin@test.com",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal());

        verify(request)
                .setAttribute(
                        "authenticatedUser",
                        "admin@test.com");

        verify(request)
                .setAttribute(
                        "authenticatedRole",
                        "ADMIN");

        verify(request)
                .setAttribute(
                        "authenticatedUserId",
                        1);

        verify(request)
                .setAttribute(
                        "X-Auth-Role",
                        "ADMIN");

        verify(request)
                .setAttribute(
                        "X-Auth-User",
                        "admin@test.com");

        verify(request)
                .setAttribute(
                        "X-Auth-UserId",
                        "1");
    }

    @Test
    void testRecruiterAuthenticationSuccess() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer recruiterToken");

        when(jwtUtil.extractUsername("recruiterToken"))
                .thenReturn("recruiter@test.com");

        when(jwtUtil.extractRole("recruiterToken"))
                .thenReturn("RECRUITER");

        when(jwtUtil.extractUserId("recruiterToken"))
                .thenReturn(10);

        when(jwtUtil.validateToken("recruiterToken"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        assertEquals(
                "recruiter@test.com",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal());
    }

    @Test
    void testCandidateAuthenticationSuccess() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer candidateToken");

        when(jwtUtil.extractUsername("candidateToken"))
                .thenReturn("candidate@test.com");

        when(jwtUtil.extractRole("candidateToken"))
                .thenReturn("CANDIDATE");

        when(jwtUtil.extractUserId("candidateToken"))
                .thenReturn(5);

        when(jwtUtil.validateToken("candidateToken"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        assertEquals(
                "candidate@test.com",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal());
    }
    @Test
    void testRecruiterDeleteJobForbidden() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/job/delete/1");

        when(request.getMethod())
                .thenReturn("DELETE");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer recruiterToken");

        when(jwtUtil.extractUsername("recruiterToken"))
                .thenReturn("recruiter@test.com");

        when(jwtUtil.extractRole("recruiterToken"))
                .thenReturn("RECRUITER");

        when(jwtUtil.extractUserId("recruiterToken"))
                .thenReturn(10);

        when(jwtUtil.validateToken("recruiterToken"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Access Denied: Insufficient permissions",
                responseWriter.toString());
    }

    @Test
    void testCandidateCreateJobForbidden() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/job/add");

        when(request.getMethod())
                .thenReturn("POST");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer candidateToken");

        when(jwtUtil.extractUsername("candidateToken"))
                .thenReturn("candidate@test.com");

        when(jwtUtil.extractRole("candidateToken"))
                .thenReturn("CANDIDATE");

        when(jwtUtil.extractUserId("candidateToken"))
                .thenReturn(5);

        when(jwtUtil.validateToken("candidateToken"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Access Denied: Insufficient permissions",
                responseWriter.toString());
    }

    @Test
    void testUnknownRoleForbidden() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtUtil.extractUsername("token"))
                .thenReturn("user@test.com");

        when(jwtUtil.extractRole("token"))
                .thenReturn("UNKNOWN");

        when(jwtUtil.extractUserId("token"))
                .thenReturn(1);

        when(jwtUtil.validateToken("token"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Access Denied: Insufficient permissions",
                responseWriter.toString());
    }

    @Test
    void testNullRoleForbidden() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/jobs/jobs/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtUtil.extractUsername("token"))
                .thenReturn("user@test.com");

        when(jwtUtil.extractRole("token"))
                .thenReturn(null);

        when(jwtUtil.extractUserId("token"))
                .thenReturn(1);

        when(jwtUtil.validateToken("token"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                response,
                filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());

        assertEquals(
                "Access Denied: Insufficient permissions",
                responseWriter.toString());
    }
    @Test
    void testRecruiterCanViewJobNotifications() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/notification/job/1");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer recruiterToken");

        when(jwtUtil.extractUsername("recruiterToken"))
                .thenReturn("recruiter@test.com");

        when(jwtUtil.extractRole("recruiterToken"))
                .thenReturn("RECRUITER");

        when(jwtUtil.extractUserId("recruiterToken"))
                .thenReturn(10);

        when(jwtUtil.validateToken("recruiterToken"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void testCandidateCanViewOwnNotifications() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/notification/candidate/5");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer candidateToken");

        when(jwtUtil.extractUsername("candidateToken"))
                .thenReturn("candidate@test.com");

        when(jwtUtil.extractRole("candidateToken"))
                .thenReturn("CANDIDATE");

        when(jwtUtil.extractUserId("candidateToken"))
                .thenReturn(5);

        when(jwtUtil.validateToken("candidateToken"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void testRecruiterCannotViewCandidateNotifications() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/notification/candidate/5");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer recruiterToken");

        when(jwtUtil.extractUsername("recruiterToken"))
                .thenReturn("recruiter@test.com");

        when(jwtUtil.extractRole("recruiterToken"))
                .thenReturn("RECRUITER");

        when(jwtUtil.extractUserId("recruiterToken"))
                .thenReturn(10);

        when(jwtUtil.validateToken("recruiterToken"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());
    }

    @Test
    void testCandidateCannotViewAllNotifications() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/api/notification/all");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer candidateToken");

        when(jwtUtil.extractUsername("candidateToken"))
                .thenReturn("candidate@test.com");

        when(jwtUtil.extractRole("candidateToken"))
                .thenReturn("CANDIDATE");

        when(jwtUtil.extractUserId("candidateToken"))
                .thenReturn(5);

        when(jwtUtil.validateToken("candidateToken"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_FORBIDDEN);

        verify(filterChain, never())
                .doFilter(any(), any());
    }
}