package com.akuev.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Глобальный фильтр для отслеживания и коррекции запросов в Spring Cloud Gateway.
 * Обеспечивает сквозную идентификацию запросов и извлечение информации из JWT-токенов.
 */
@Order(1)
@Slf4j
@Component
public class TrackingFilter implements GlobalFilter {

    private final FilterUtils filterUtils;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param filterUtils утилита для работы с заголовками фильтров
     */
    public TrackingFilter(FilterUtils filterUtils) {
        this.filterUtils = filterUtils;
    }

    /**
     * Фильтрует входящие HTTP-запросы, добавляя или извлекая идентификатор корреляции.
     * Также извлекает информацию о пользователе из JWT-токена авторизации.
     *
     * @param exchange объект ServerWebExchange, представляющий HTTP-запрос и ответ
     * @param chain цепочка фильтров Gateway
     * @return Mono<Void> для асинхронной обработки
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        // Проверяем и устанавливаем идентификатор корреляции
        if (isCorrelationIdPresent(requestHeaders)) {
            log.debug("cinema-correlation-id found in tracking filter: {}",
                    filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationId);
            log.debug("cinema-correlation-id generated in tracking filter: {}", correlationId);
        }

        // Извлекаем и логируем имя пользователя из JWT-токена
        try {
            System.out.println("The authentication name from the token is : " + getUsername(requestHeaders));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return chain.filter(exchange);
    }

    /**
     * Проверяет наличие идентификатора корреляции в заголовках запроса.
     *
     * @param requestHeaders заголовки HTTP-запроса
     * @return true, если идентификатор корреляции присутствует, false - в противном случае
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterUtils.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Генерирует уникальный идентификатор корреляции.
     *
     * @return строковое представление UUID
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Извлекает имя пользователя из JWT-токена авторизации.
     *
     * @param requestHeaders заголовки HTTP-запроса
     * @return имя пользователя или пустую строку, если токен отсутствует
     * @throws JSONException если возникает ошибка парсинга JWT-токена
     */
    private String getUsername(HttpHeaders requestHeaders) throws JSONException {
        String username = "";
        if (filterUtils.getAuthToken(requestHeaders) != null) {
            String authToken = filterUtils
                    .getAuthToken(requestHeaders)
                    .replace("Bearer ", "");
            JSONObject jsonObject = decodeJWT(authToken);
            try {
                // Извлекаем preferred_username из claims JWT-токена
                username = jsonObject.getString("preferred_username");
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }

        return username;
    }

    /**
     * Декодирует JWT-токен и преобразует его тело в JSONObject.
     *
     * @param JWTToken строковое представление JWT-токена
     * @return JSONObject с claims из тела JWT-токена
     * @throws JSONException если возникает ошибка парсинга JSON
     */
    private JSONObject decodeJWT(String JWTToken) throws JSONException {
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        JSONObject jsonObject = new JSONObject(body);
        return jsonObject;
    }
}