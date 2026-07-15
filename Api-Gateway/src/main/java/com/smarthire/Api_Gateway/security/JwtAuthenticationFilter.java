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
        String method = request.getMethod();

        log.info("Incoming request: {} {}", method, path);

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
                response.getWriter().write("Invalid or Expired JWT Token");
                return;
            }

            if (!isAuthorized(path, method, role)) {
                log.warn("Access denied for user: {} with role: {} on {} {}", username, role, method, path);
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

            log.info("Authentication successful for user: {} with role: {}", username, role);

        } catch (Exception e) {
            log.error("JWT authentication failed", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorized(String path, String method, String role) {

        if (role == null) return false;

        switch (role) {

            case "ADMIN":
                return true;

            case "RECRUITER":

                if (path.startsWith("/jobs")) {
                    if (method.equals("DELETE")) return false;
                    return true;
                }

                if (path.startsWith("/api/candidates") || path.startsWith("/api/candidate")) {
                    if (path.equals("/api/candidate/add") && method.equals("POST")) return false;
                    if (path.startsWith("/api/candidate/update") && method.equals("PUT")) return false;
                    if (path.startsWith("/api/candidate/apply")) return false;
                    if (method.equals("DELETE")) return false;
                    if (path.contains("/activate") || path.contains("/deactivate")) return false;
                    if (path.startsWith("/api/candidate/page") ||
                            path.startsWith("/api/candidate/sort")) return false;
                    if (path.contains("/hire") && method.equals("PATCH")) return true;
                    return method.equals("GET");
                }

                if (path.startsWith("/resumes")) {
                    return method.equals("GET");
                }

                if (path.startsWith("/api/screening")) {
                    if (method.equals("DELETE")) return false;
                    return true;
                }

                if (path.startsWith("/api/notification")) {
                    if (path.equals("/api/notification/all")) return false;
                    if (path.equals("/api/notification/add")) return false;
                    if (method.equals("DELETE")) return false;
                    if (path.startsWith("/api/notification/candidate/")) return true;
                    if (path.startsWith("/api/notification/job/")) return true;
                    if (path.equals("/api/notification/unread")) return true;
                    if (path.matches("/api/notification/[^/]+/read")) return true;
                    return false;
                }

                return false;

            case "CANDIDATE":

                if (path.startsWith("/jobs")) {
                    if (method.equals("POST") || method.equals("PUT") ||
                            method.equals("DELETE") || method.equals("PATCH")) return false;
                    return method.equals("GET");
                }

                if (path.startsWith("/api/candidate") || path.startsWith("/api/candidates")) {
                    if (path.equals("/api/candidates/all")) return false;
                    if (method.equals("DELETE")) return false;
                    if (path.contains("/activate") || path.contains("/deactivate") ||
                            path.contains("/hire")) return false;
                    if (path.startsWith("/api/candidate/search") ||
                            path.startsWith("/api/candidate/skill") ||
                            path.startsWith("/api/candidate/location") ||
                            path.startsWith("/api/candidate/experience") ||
                            path.startsWith("/api/candidate/status") ||
                            path.startsWith("/api/candidate/page") ||
                            path.startsWith("/api/candidate/sort")) return false;
                    if (path.equals("/api/candidate/add") && method.equals("POST")) return true;
                    if (path.startsWith("/api/candidate/update") && method.equals("PUT")) return true;
                    if (path.startsWith("/api/candidate/apply") && method.equals("POST")) return true;
                    if (method.equals("GET")) return true;
                    return false;
                }

                if (path.startsWith("/resumes")) {
                    return true;
                }

                if (path.startsWith("/api/screening")) {
                    return false;
                }

                if (path.startsWith("/api/notification")) {
                    if (path.equals("/api/notification/all")) return false;
                    if (path.equals("/api/notification/add")) return false;
                    if (method.equals("DELETE")) return false;
                    if (path.startsWith("/api/notification/job/")) return false;
                    if (path.startsWith("/api/notification/candidate/")) return true;
                    if (path.equals("/api/notification/unread")) return true;
                    if (path.matches("/api/notification/[^/]+/read")) return true;
                    return false;
                }

                return false;

            default:
                return false;
        }
    }
}