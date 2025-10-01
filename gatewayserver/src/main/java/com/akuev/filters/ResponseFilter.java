package com.akuev.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class ResponseFilter {
    private final FilterUtils filterUtils;

    public ResponseFilter(FilterUtils filterUtils) {
        this.filterUtils = filterUtils;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders headers = exchange.getRequest().getHeaders();
                String correlationId = filterUtils.getCorrelationId(headers);
                log.debug("Adding the correlation id to the outbound headers. {}", correlationId);
                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
                log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
            }
            ));
        };
    }
}
