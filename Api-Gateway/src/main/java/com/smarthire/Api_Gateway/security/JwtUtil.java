package com.smarthire.Api_Gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {

        log.debug("Extracting claims from JWT token");

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    public String extractUsername(String token) {

        String username = extractClaim(token, Claims::getSubject);

        log.debug("Username extracted from token: {}", username);

        return username;
    }

    public String extractRole(String token) {

        String role = extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );

        log.debug("Role extracted from token: {}", role);

        return role;
    }

    public Integer extractUserId(String token) {

        Integer userId = extractClaim(
                token,
                claims -> claims.get("userId", Integer.class)
        );

        log.debug("User ID extracted from token: {}", userId);

        return userId;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {

        boolean expired = extractExpiration(token).before(new Date());

        if (expired) {
            log.warn("JWT token has expired");
        }

        return expired;
    }

    public boolean validateToken(String token) {

        boolean valid = !isTokenExpired(token);

        if (valid) {
            log.debug("JWT token validated successfully");
        }

        return valid;
    }
}