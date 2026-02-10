package com.akuev.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Утилитарный класс для работы с HTTP-заголовками в фильтрах.
 * Предоставляет константы и методы для обработки контекстных заголовков в микросервисной архитектуре.
 */
@Component
public class FilterUtils {

    /**
     * Название заголовка для идентификатора корреляции запроса.
     */
    public static final String CORRELATION_ID = "cinema-correlation-id";

    /**
     * Название заголовка для токена авторизации.
     */
    public static final String AUTH_TOKEN     = "Authorization";

    /**
     * Название заголовка для идентификатора пользователя.
     */
    public static final String USER_ID        = "cinema-user-id";

    /**
     * Название заголовка для идентификатора бронирования.
     */
    public static final String BOOKING_ID     = "cinema-booking-id";

    /**
     * Название заголовка для идентификатора сеанса.
     */
    public static final String SESSION_ID     = "cinema-session-id";

    /**
     * Название заголовка для идентификатора фильма.
     */
    public static final String MOVIE_ID       = "cinema-movie-id";

    /**
     * Извлекает идентификатор корреляции из заголовков HTTP-запроса.
     *
     * @param requestHeaders заголовки HTTP-запроса
     * @return идентификатор корреляции или null, если заголовок отсутствует
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) == null) {
            return null;
        } else {
            List<String> headers = requestHeaders.get(CORRELATION_ID);
            return headers.stream().findFirst().get();
        }
    }

    /**
     * Извлекает токен авторизации из заголовков HTTP-запроса.
     *
     * @param requestHeaders заголовки HTTP-запроса
     * @return токен авторизации или null, если заголовок отсутствует
     */
    public String getAuthToken(HttpHeaders requestHeaders) {
        if (requestHeaders.get(AUTH_TOKEN) == null) {
            return null;
        } else {
            List<String> headers = requestHeaders.get(AUTH_TOKEN);
            return headers.stream().findFirst().get();
        }
    }

    /**
     * Устанавливает HTTP-заголовок в запросе ServerWebExchange.
     *
     * @param exchange объект ServerWebExchange
     * @param name название заголовка
     * @param value значение заголовка
     * @return измененный объект ServerWebExchange с установленным заголовком
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header(name, value)
                                .build())
                .build();
    }

    /**
     * Устанавливает заголовок идентификатора корреляции в запросе ServerWebExchange.
     *
     * @param exchange объект ServerWebExchange
     * @param correlationId идентификатор корреляции
     * @return измененный объект ServerWebExchange с установленным заголовком корреляции
     */
    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }
}