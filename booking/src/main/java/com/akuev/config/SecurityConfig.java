package com.akuev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Конфигурационный класс для настройки Spring Security.
 * Настраивает аутентификацию через Keycloak (JWT) и авторизацию на основе ролей.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    /**
     * Создает цепочку фильтров безопасности для HTTP-запросов.
     *
     * @param http объект для настройки безопасности HTTP
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если возникла ошибка при настройке
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Настраиваем OAuth2 Resource Server для работы с JWT-токенами
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     * Создает конвертер для аутентификации JWT.
     * Извлекает роли из JWT-токена и преобразует их в GrantedAuthority для Spring Security.
     *
     * @return конвертер JWT аутентификации
     */
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