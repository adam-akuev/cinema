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
 * Используется с RestTemplate для передачи контекстной информации между микросервисами.
 */
@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Перехватывает HTTP-запрос и добавляет контекстные заголовки перед его отправкой.
     * Извлекает данные из UserContextHolder и добавляет их в заголовки запроса.
     *
     * @param request HTTP-запрос для перехвата
     * @param body тело запроса
     * @param execution объект для выполнения запроса
     * @return HTTP-ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        HttpHeaders headers = request.getHeaders();

        // Добавляем контекстные заголовки из UserContextHolder
        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

        log.debug("UserContextInterceptor get CORRELATION_ID={} and AUTH_TOKEN={}",
                UserContext.CORRELATION_ID, UserContext.AUTH_TOKEN);

        // Продолжаем выполнение запроса с добавленными заголовками
        return execution.execute(request, body);
    }
}