package com.smarthire.auth_service.service;

import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository repo;
    private final JWTService jwtService;
    private final AuthenticationManager auth;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder(12);

    public UserService(UserRepository repo,
                       JWTService jwtService,
                       AuthenticationManager auth) {
        this.repo = repo;
        this.jwtService = jwtService;
        this.auth = auth;
    }

    public Users register(@Valid Users user) {

        log.info("Registering user with email: {}", user.getEmail());

        user.setPassword(encoder.encode(user.getPassword()));

        Users savedUser = repo.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());

        return savedUser;
    }

    public String login(Users user) {

        log.info("Login attempt for user: {}", user.getEmail());

        auth.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        log.info("Authentication successful for user: {}", user.getEmail());

        Users dbUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found: {}", user.getEmail());
                    return new RuntimeException("User not found");
                });

        String token = jwtService.generateToken(dbUser);

        log.info("Login successful. JWT generated for user: {}", user.getEmail());

        return token;
    }
}