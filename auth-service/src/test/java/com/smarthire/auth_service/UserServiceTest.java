package com.smarthire.auth_service;

import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.repository.UserRepository;
import com.smarthire.auth_service.service.JWTService;
import com.smarthire.auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager auth;

    @InjectMocks
    private UserService userService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@smarthire.com");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole(Users.Role.CANDIDATE);
    }

    // register() tests

    @Test
    void register_ShouldEncodePassword_AndSaveUser() {

        Users savedUser = new Users();
        savedUser.setEmail("test@smarthire.com");
        savedUser.setPassword("$2a$12$encodedpassword");
        savedUser.setName("Test User");
        savedUser.setRole(Users.Role.CANDIDATE);

        when(repo.save(any(Users.class))).thenReturn(savedUser);

        Users result = userService.register(testUser);

        assertNotNull(result);
        assertEquals("test@smarthire.com", result.getEmail());
        assertNotEquals("password123", result.getPassword());
        assertTrue(result.getPassword().startsWith("$2a$"));
        verify(repo, times(1)).save(any(Users.class));
    }

    @Test
    void register_ShouldReturnSavedUser_WithCorrectEmail() {

        Users savedUser = new Users();
        savedUser.setEmail("test@smarthire.com");
        savedUser.setPassword("$2a$12$encodedpassword");
        savedUser.setName("Test User");
        savedUser.setRole(Users.Role.CANDIDATE);

        when(repo.save(any(Users.class))).thenReturn(savedUser);

        Users result = userService.register(testUser);

        assertEquals("test@smarthire.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(Users.Role.CANDIDATE, result.getRole());
    }

    @Test
    void register_ShouldCallRepositorySave_ExactlyOnce() {

        when(repo.save(any(Users.class))).thenReturn(testUser);

        userService.register(testUser);

        verify(repo, times(1)).save(any(Users.class));
    }

    @Test
    void register_ShouldNeverStoreRawPassword() {

        String rawPassword = "password123";
        testUser.setPassword(rawPassword);

        when(repo.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            assertNotEquals(rawPassword, u.getPassword());
            return u;
        });

        userService.register(testUser);

        verify(repo, times(1)).save(any(Users.class));
    }

    // login() tests

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {

        Users dbUser = new Users();
        dbUser.setEmail("test@smarthire.com");
        dbUser.setPassword("$2a$12$encodedpassword");
        dbUser.setRole(Users.Role.CANDIDATE);

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(dbUser));
        when(jwtService.generateToken(dbUser))
                .thenReturn("mocked.jwt.token");

        String token = userService.login(testUser);

        assertNotNull(token);
        assertEquals("mocked.jwt.token", token);
    }

    @Test
    void login_ShouldCallAuthenticationManager_WithCorrectCredentials() {

        Users dbUser = new Users();
        dbUser.setEmail("test@smarthire.com");
        dbUser.setPassword("$2a$12$encodedpassword");
        dbUser.setRole(Users.Role.CANDIDATE);

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(dbUser));
        when(jwtService.generateToken(dbUser))
                .thenReturn("mocked.jwt.token");

        userService.login(testUser);

        verify(auth, times(1)).authenticate(
                argThat(token ->
                        token.getPrincipal().equals("test@smarthire.com") &&
                                token.getCredentials().equals("password123")
                )
        );
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(testUser));

        verify(repo, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_ShouldThrowRuntimeException_WhenUserNotFoundInDatabase() {

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(testUser)
        );

        assertEquals("User not found", exception.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_ShouldCallJwtService_AfterSuccessfulAuthentication() {

        Users dbUser = new Users();
        dbUser.setEmail("test@smarthire.com");
        dbUser.setPassword("$2a$12$encodedpassword");
        dbUser.setRole(Users.Role.CANDIDATE);

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(dbUser));
        when(jwtService.generateToken(dbUser))
                .thenReturn("mocked.jwt.token");

        userService.login(testUser);

        verify(jwtService, times(1)).generateToken(dbUser);
    }

    @Test
    void login_ShouldNeverCallJwtService_WhenAuthenticationFails() {

        when(auth.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(testUser));

        verify(jwtService, never()).generateToken(any());
    }
}