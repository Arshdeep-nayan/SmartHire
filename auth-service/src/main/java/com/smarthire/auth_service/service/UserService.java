package com.smarthire.auth_service.service;

import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String login(Users user) {

        auth.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        Users dbUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return jwtService.generateToken(dbUser);
    }
}