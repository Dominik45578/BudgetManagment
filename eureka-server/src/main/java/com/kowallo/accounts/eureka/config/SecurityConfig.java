package com.kowallo.accounts.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Servlet-based security configuration for the Eureka Server.
 *
 * <p>Eureka Server runs on the Servlet stack (Tomcat) because
 * {@code spring-cloud-starter-netflix-eureka-server} transitively pulls in
 * {@code spring-boot-starter-web}. Therefore, we use the standard
 * {@link HttpSecurity} (not the reactive {@code ServerHttpSecurity}).</p>
 *
 * <p>Current setup:</p>
 * <ul>
 *   <li>Actuator endpoints are permitted without authentication (needed for health checks)</li>
 *   <li>All other endpoints (dashboard, registry /eureka/**) require HTTP Basic authentication</li>
 *   <li>CSRF is disabled because Eureka clients communicate via REST — no browser forms</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}

