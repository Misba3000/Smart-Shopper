package com.smartShopper.smart_price_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for API endpoints
                .csrf(csrf -> csrf.disable())

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Allow all requests without authentication
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                )

                // Disable form login
                .formLogin(form -> form.disable())

                // Disable HTTP Basic authentication
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}