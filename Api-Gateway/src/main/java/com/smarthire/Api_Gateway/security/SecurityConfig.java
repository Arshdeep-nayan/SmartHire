package com.smarthire.Api_Gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .httpBasic(basic -> basic.disable())

                .formLogin(form -> form.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(


                                "/api/login",
                                "/api/register",


                                "/eureka/**",


                                "/actuator/**",


                                "/swagger-ui/**",
                                "/swagger-ui.html",


                                "/v3/api-docs/**",


                                "/auth/v3/api-docs",
                                "/jobs/v3/api-docs",
                                "/candidate/v3/api-docs",
                                "/screening/v3/api-docs",
                                "/notification/v3/api-docs"

                        ).permitAll()

                        .anyRequest()

                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}