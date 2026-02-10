package com.akuev.config;

import com.akuev.util.UserContext;
import com.akuev.util.UserContextHolder;
import feign.RequestInterceptor;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Конфигурационный класс для настройки Feign клиентов и балансировки нагрузки.
 * Также содержит интерсепторы для добавления контекстных заголовков в HTTP-запросы.
 */
@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "movie-service"),
        @LoadBalancerClient(name = "user-service")
})
public class FeignConfig {

    /**
     * Создает интерсептор для добавления контекста пользователя в заголовки Feign-запросов.
     * Добавляет идентификатор корреляции, токен авторизации и идентификатор пользователя.
     *
     * @return RequestInterceptor для добавления контекстных заголовков
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            UserContext context = UserContextHolder.getContext();

            if (context.getCorrelationId() != null) {
                template.header(UserContext.CORRELATION_ID, context.getCorrelationId());
            }
            if (context.getAuthToken() != null) {
                template.header(UserContext.AUTH_TOKEN, context.getAuthToken());
            }
            if (context.getUserId() != null) {
                template.header(UserContext.USER_ID, context.getUserId());
            }

            System.out.println("Feign Headers - CorrelationId: " + context.getCorrelationId());
        };
    }

    /**
     * Создает интерсептор для добавления JWT-токена авторизации в заголовки Feign-запросов.
     * Извлекает токен из контекста безопасности и добавляет его в заголовок Authorization.
     *
     * @return RequestInterceptor для добавления JWT-токена
     */
    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String tokenValue = jwtAuth.getToken().getTokenValue();
                template.header("Authorization", "Bearer " + tokenValue);

                System.out.println("JWT TOKEN ADDED");
            }
        };
    }

    /**
     * Создает интерсептор для добавления API-ключа в заголовки Feign-запросов.
     * Используется для аутентификации внутренних вызовов между микросервисами.
     *
     * @return RequestInterceptor для добавления API-ключа
     */
    @Bean
    public RequestInterceptor apiKeyRequestInterceptor() {
        return template -> {
            // TODO: Вынести ключ в конфигурацию и шифровать
            template.header("X-API-Key", "cinema-internal-secret-key-2024");
            System.out.println("API Key added to Feign request");
        };
    }
}