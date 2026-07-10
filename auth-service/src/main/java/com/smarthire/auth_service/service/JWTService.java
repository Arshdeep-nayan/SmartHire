package com.smarthire.auth_service.service;

import com.smarthire.auth_service.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Users user) {

        log.info("Generating JWT for user: {}", user.getEmail());

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey())
                .compact();

        log.info("JWT generated successfully for user: {}", user.getEmail());

        return token;
    }

    private Claims extractAllClaims(String token) {
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

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token,
                claims -> claims.get("role", String.class));
    }

    public Integer extractUserId(String token) {
        return extractClaim(token,
                claims -> claims.get("userId", Integer.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails
    ) {

        log.debug("Validating JWT for user: {}", userDetails.getUsername());

        final String username = extractUserName(token);

        boolean isValid = username.equals(userDetails.getUsername())
                && !isTokenExpired(token);

        if (isValid) {
            log.info("JWT validated successfully for user: {}", username);
        } else {
            log.warn("JWT validation failed for user: {}", userDetails.getUsername());
        }

        return isValid;
    }

    public boolean validateToken(String token) {

        boolean isValid = !isTokenExpired(token);

        if (isValid) {
            log.debug("JWT validated successfully.");
        } else {
            log.warn("JWT has expired.");
        }

        return isValid;
    }
}