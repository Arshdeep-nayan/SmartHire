package com.smarthire.Api_Gateway;

import com.smarthire.Api_Gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private SecretKey key;

    private static final String SECRET =
            "VGhpc0lzQVN1cGVyU2VjcmV0S2V5Rm9yU21hcnRIaXJlSldUQXV0aA==";

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(
                jwtUtil,
                "secretKey",
                SECRET);

        key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET));
    }

    private String generateToken() {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", "ADMIN");
        claims.put("userId", 1);

        return Jwts.builder()
                .claims(claims)
                .subject("admin@smarthire.com")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 600000))
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractUsername() {

        String token = generateToken();

        assertEquals(
                "admin@smarthire.com",
                jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractRole() {

        String token = generateToken();

        assertEquals(
                "ADMIN",
                jwtUtil.extractRole(token));
    }

    @Test
    void testExtractUserId() {

        String token = generateToken();

        assertEquals(
                1,
                jwtUtil.extractUserId(token));
    }
    @Test
    void testExtractAllClaims() {

        String token = generateToken();

        Claims claims =
                jwtUtil.extractAllClaims(token);

        assertNotNull(claims);

        assertEquals(
                "admin@smarthire.com",
                claims.getSubject());

        assertEquals(
                "ADMIN",
                claims.get("role", String.class));

        assertEquals(
                1,
                claims.get("userId", Integer.class));
    }

    @Test
    void testExtractExpiration() {

        String token = generateToken();

        Date expiration =
                jwtUtil.extractExpiration(token);

        assertNotNull(expiration);

        assertTrue(
                expiration.after(new Date()));
    }

    @Test
    void testValidateToken() {

        String token = generateToken();

        assertTrue(
                jwtUtil.validateToken(token));
    }

    @Test
    void testInvalidTokenThrowsException() {

        assertThrows(
                Exception.class,
                () -> jwtUtil.extractUsername("invalid.jwt.token"));
    }
}