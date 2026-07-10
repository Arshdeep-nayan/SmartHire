package com.smarthire.Api_Gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            RouteValidator routeValidator
    ) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        log.info("Incoming request: {}", path);

        if (!routeValidator.isSecured.test(path)) {

            log.info("Public endpoint accessed: {}", path);

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.warn("Missing Authorization header for request: {}", path);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization Header");
            return;
        }

        String token = authHeader.substring(7);

        try {

            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            Integer userId = jwtUtil.extractUserId(token);

            log.info("Authenticating user: {} with role: {}", username, role);

            if (!jwtUtil.validateToken(token)) {

                log.warn("Invalid JWT token for user: {}", username);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }

            if (!isAuthorized(path, role)) {

                log.warn(
                        "Access denied for user: {} with role: {} on path: {}",
                        username,
                        role,
                        path);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Insufficient permissions");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("authenticatedUser", username);
            request.setAttribute("authenticatedRole", role);
            request.setAttribute("authenticatedUserId", userId);

            request.setAttribute("X-Auth-Role", role);
            request.setAttribute("X-Auth-User", username);
            request.setAttribute("X-Auth-UserId", String.valueOf(userId));

            log.info(
                    "Authentication successful for user: {} with role: {}",
                    username,
                    role);

        }
        catch (Exception e) {

            log.error("JWT authentication failed", e);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorized(String path, String role) {

        if (role == null) {
            return false;
        }

        switch (role) {

            case "ADMIN":

                return true;

            case "RECRUITER":

                return path.startsWith("/jobs") ||
                        path.startsWith("/candidates") ||
                        path.startsWith("/resumes") ||
                        path.startsWith("/api/screening") ||
                        path.startsWith("/api/notifications");

            case "CANDIDATE":

                return path.startsWith("/jobs/jobs/all") ||
                        path.startsWith("/jobs/active") ||
                        path.startsWith("/jobs/search") ||
                        path.startsWith("/jobs/location") ||
                        path.startsWith("/jobs/type") ||
                        path.startsWith("/jobs/experience") ||
                        path.startsWith("/jobs/page") ||
                        path.startsWith("/jobs/sort") ||
                        path.startsWith("/jobs/salary") ||
                        path.startsWith("/jobs/company") ||
                        path.startsWith("/candidates") ||
                        path.startsWith("/resumes") ||
                        path.startsWith("/api/screening") ||
                        path.startsWith("/api/notifications");

            default:

                return false;
        }
    }
}