package com.akuev.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * HTTP-интерцептор для добавления контекста пользователя в исходящие запросы.
 * Добавляет заголовки корреляции и аутентификации в REST-вызовы.
 */
@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Добавляет контекстные заголовки в исходящий HTTP-запрос.
     *
     * @param request исходный HTTP-запрос
     * @param body тело запроса
     * @param execution цепочка выполнения запроса
     * @return ответ на запрос
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();

        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());
        log.debug("UserContextInterceptor get CORRELATION_ID={} and AUTH_TOKEN={}",
                UserContextHolder.getContext().getCorrelationId(),
                UserContextHolder.getContext().getAuthToken());

        return execution.execute(request, body);
    }
}