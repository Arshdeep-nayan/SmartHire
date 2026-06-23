package com.smarthire.auth_service.service;

import com.smarthire.auth_service.model.UserPrincipal;
import com.smarthire.auth_service.model.Users;
import com.smarthire.auth_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService
{
    private final UserRepository repo;
    public MyUserDetailsService(UserRepository repo){
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found!!"));

        return new UserPrincipal(user);
    }
}
