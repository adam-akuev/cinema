package com.akuev.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Перехватчик HTTP-запросов для добавления контекста пользователя в заголовки исходящих запросов.
 * Используется в RestTemplate для передачи контекста между микросервисами.
 */
@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Перехватывает исходящий HTTP-запрос и добавляет контекст пользователя в заголовки.
     *
     * @param request исходный HTTP-запрос
     * @param body тело запроса
     * @param execution объект для выполнения запроса
     * @return ответ на HTTP-запрос
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        HttpHeaders headers = request.getHeaders();

        // Добавляем контекст пользователя в заголовки запроса
        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

        log.debug("UserContextInterceptor get CORRELATION_ID={} and AUTH_TOKEN={}",
                UserContext.CORRELATION_ID, UserContext.AUTH_TOKEN);

        // Продолжаем выполнение запроса
        return execution.execute(request, body);
    }
}