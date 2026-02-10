package com.akuev.filters;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * Конфигурационный класс для фильтра ответов в Spring Cloud Gateway.
 * Добавляет идентификатор корреляции в заголовки исходящих HTTP-ответов.
 */
@Slf4j
@Configuration
public class ResponseFilter {
    private final FilterUtils filterUtils;
    private final Tracer tracer;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param filterUtils утилита для работы с заголовками фильтров
     * @param tracer трассировщик для работы с трассировкой запросов
     */
    public ResponseFilter(FilterUtils filterUtils, Tracer tracer) {
        this.filterUtils = filterUtils;
        this.tracer = tracer;
    }

    /**
     * Создает глобальный фильтр для обработки исходящих HTTP-ответов.
     * Фильтр добавляет идентификатор корреляции (trace ID из Zipkin/Sleuth)
     * в заголовки ответов для сквозной трассировки запросов в микросервисной архитектуре.
     *
     * @return глобальный фильтр Spring Cloud Gateway
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Добавляем trace ID из Zipkin/Sleuth в заголовки ответа
                if (tracer.currentSpan() != null) {
                    String traceId = tracer.currentSpan().context().traceIdString();
                    log.debug("Adding the correlation id to the outbound headers. {}", traceId);
                    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                } else {
                    // Если span не доступен, используем correlation ID из заголовков запроса
                    log.warn("This span = null");
                    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID,
                            filterUtils.getCorrelationId(new HttpHeaders()));
                }
                log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
            }));
        };
    }
}