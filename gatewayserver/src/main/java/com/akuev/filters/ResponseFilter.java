package com.akuev.filters;

import brave.Tracer;
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
    private final Tracer tracer;

    public ResponseFilter(FilterUtils filterUtils, Tracer tracer) {
        this.filterUtils = filterUtils;
        this.tracer = tracer;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (tracer.currentSpan() != null) {
                    String traceId = tracer.currentSpan().context().traceIdString();
                    log.debug("Adding the correlation id to the outbound headers. {}", traceId);
                    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                } else {
                    log.warn("This span = null");
                    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId(new HttpHeaders()));
                }
                log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
            }
            ));
        };
    }
}
