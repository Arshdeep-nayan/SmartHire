package com.smarthire.auth_service;

import com.smarthire.auth_service.model.UserPrincipal;
import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.repository.UserRepository;
import com.smarthire.auth_service.service.MyUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@smarthire.com");
        testUser.setPassword("$2a$12$encodedpassword");
        testUser.setName("Test User");
        testUser.setRole(Users.Role.CANDIDATE);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserPrincipal_WhenUserExists() {
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails result = myUserDetailsService.loadUserByUsername("test@smarthire.com");

        assertNotNull(result);
        assertInstanceOf(UserPrincipal.class, result);
        assertEquals("test@smarthire.com", result.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        when(repo.findByEmail("unknown@smarthire.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername("unknown@smarthire.com")
        );

        assertEquals("User not found!!", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldCallRepository_WithCorrectEmail() {
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(testUser));

        myUserDetailsService.loadUserByUsername("test@smarthire.com");

        verify(repo, times(1)).findByEmail("test@smarthire.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WithCorrectRole() {
        when(repo.findByEmail("test@smarthire.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails result = myUserDetailsService.loadUserByUsername("test@smarthire.com");

        assertFalse(result.getAuthorities().isEmpty());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CANDIDATE")));
    }
}