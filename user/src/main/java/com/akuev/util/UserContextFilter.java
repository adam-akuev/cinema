package com.akuev.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet фильтр для инициализации контекста пользователя из HTTP-заголовков.
 * Извлекает данные из заголовков запроса и сохраняет их в {@link UserContextHolder}.
 */
@Slf4j
@Component
public class UserContextFilter implements Filter {

    /**
     * Обрабатывает входящий запрос, извлекая контекстные данные из заголовков.
     *
     * @param servletRequest входящий запрос
     * @param servletResponse исходящий ответ
     * @param filterChain цепочка фильтров
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext().setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
        UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setBookingId(httpServletRequest.getHeader(UserContext.BOOKING_ID));

        log.debug("UserContextFilter Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}