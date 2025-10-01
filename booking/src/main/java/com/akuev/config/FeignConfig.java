package com.akuev.config;

import com.akuev.util.UserContext;
import com.akuev.util.UserContextHolder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignConfig {
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
}
