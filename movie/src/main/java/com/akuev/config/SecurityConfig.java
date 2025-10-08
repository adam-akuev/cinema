package com.akuev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/v1/movie-sessions/internal/**").access(apiKeyAuthorizationManager())
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> apiKeyAuthorizationManager() {
        return (authentication, context) -> {
            String apiKey = context.getRequest().getHeader("X-API-Key");

            if ("cinema-internal-secret-key-2024".equals(apiKey)) {
                var internalServiceAuth = new UsernamePasswordAuthenticationToken(
                        "INTERNAL_SERVICE",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                );
                SecurityContextHolder.getContext().setAuthentication(internalServiceAuth);
                return new AuthorizationDecision(true);
            }
            return new AuthorizationDecision(false);
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            // Извлекаем роли из resource_access.akuev-cinema.roles
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            if (resourceAccess != null && resourceAccess.get("akuev-cinema") != null) {
                Map<String, Object> client = (Map<String, Object>) resourceAccess.get("akuev-cinema");
                List<String> roles = (List<String>) client.get("roles");

                if (roles != null) {
                    for (String role : roles) {
                        // Добавляем префикс ROLE_ для Spring Security
                        String authority = "ROLE_" + role;
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                }
            }

            System.out.println("Final authorities: " + authorities);
            return authorities;
        });

        return converter;
    }
}
