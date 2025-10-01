package com.akuev.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtils {
    public static final String CORRELATION_ID = "cinema-correlation-id";
    public static final String AUTH_TOKEN     = "cinema-auth-token";
    public static final String USER_ID        = "cinema-user-id";
    public static final String BOOKING_ID     = "cinema-booking-id";
    public static final String SESSION_ID     = "cinema-session-id";
    public static final String MOVIE_ID       = "cinema-movie-id";

    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) == null) {
            return null;
        } else {
            List<String> headers = requestHeaders.get(CORRELATION_ID);
            return headers.stream().findFirst().get();
        }
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(
                exchange.getRequest().mutate()
                        .header(name, value)
                        .build())
                .build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }
}
