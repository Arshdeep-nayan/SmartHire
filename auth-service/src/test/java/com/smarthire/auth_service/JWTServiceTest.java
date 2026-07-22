package com.smarthire.auth_service;

import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    private Users testUser;
    private String validToken;

    // Same secret used in application.properties (base64 encoded)
    private static final String TEST_SECRET =
            "VGhpc0lzQVN1cGVyU2VjcmV0S2V5Rm9yU21hcnRIaXJlSldUQXV0aA==";

    private static final long TEST_EXPIRATION = 1800000L; // 30 minutes

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        testUser = new Users();
        testUser.setId(1);
        testUser.setEmail("test@smarthire.com");
        testUser.setPassword("$2a$12$encodedpassword");
        testUser.setName("Test User");
        testUser.setRole(Users.Role.CANDIDATE);

        validToken = jwtService.generateToken(testUser);
    }

    // ─────────────────────────────────────────────────────────────
    // generateToken() tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_ShouldReturnTokenWithThreeParts() {
        String token = jwtService.generateToken(testUser);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void generateToken_ShouldEmbedCorrectEmail_AsSubject() {
        String token = jwtService.generateToken(testUser);
        String extractedEmail = jwtService.extractUserName(token);
        assertEquals("test@smarthire.com", extractedEmail);
    }

    @Test
    void generateToken_ShouldEmbedCorrectRole_InClaims() {
        String token = jwtService.generateToken(testUser);
        String extractedRole = jwtService.extractRole(token);
        assertEquals("CANDIDATE", extractedRole);
    }

    @Test
    void generateToken_ShouldEmbedCorrectUserId_InClaims() {
        String token = jwtService.generateToken(testUser);
        Integer extractedUserId = jwtService.extractUserId(token);
        assertEquals(1, extractedUserId);
    }

    // ─────────────────────────────────────────────────────────────
    // extractUserName() tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void extractUserName_ShouldReturnCorrectEmail() {
        String email = jwtService.extractUserName(validToken);
        assertEquals("test@smarthire.com", email);
    }

    // ─────────────────────────────────────────────────────────────
    // extractRole() tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String role = jwtService.extractRole(validToken);
        assertEquals("CANDIDATE", role);
    }

    @Test
    void extractRole_ShouldReturnAdminRole_ForAdminUser() {
        testUser.setRole(Users.Role.ADMIN);
        String adminToken = jwtService.generateToken(testUser);
        assertEquals("ADMIN", jwtService.extractRole(adminToken));
    }

    @Test
    void extractRole_ShouldReturnRecruiterRole_ForRecruiterUser() {
        testUser.setRole(Users.Role.RECRUITER);
        String recruiterToken = jwtService.generateToken(testUser);
        assertEquals("RECRUITER", jwtService.extractRole(recruiterToken));
    }

    // ─────────────────────────────────────────────────────────────
    // extractUserId() tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        Integer userId = jwtService.extractUserId(validToken);
        assertEquals(1, userId);
    }

    // ─────────────────────────────────────────────────────────────
    // validateToken(token, userDetails) tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValidAndUserMatches() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@smarthire.com");

        boolean isValid = jwtService.validateToken(validToken, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("other@smarthire.com");

        boolean isValid = jwtService.validateToken(validToken, userDetails);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(testUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@smarthire.com");

        assertThrows(Exception.class, () -> jwtService.validateToken(expiredToken, userDetails));
    }

    // ─────────────────────────────────────────────────────────────
    // validateToken(token) tests — Gateway version
    // ─────────────────────────────────────────────────────────────

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        boolean isValid = jwtService.validateToken(validToken);
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired_1() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(testUser);

        assertThrows(Exception.class, () -> jwtService.validateToken(expiredToken));
    }
}