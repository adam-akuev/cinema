package com.akuev.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр для инициализации контекста пользователя из HTTP-заголовков.
 * Извлекает информацию из заголовков запроса и сохраняет её в UserContextHolder.
 */
@Slf4j
@Component
public class UserContextFilter implements Filter {

    /**
     * Обрабатывает входящий HTTP-запрос для инициализации контекста пользователя.
     *
     * @param servletRequest входящий запрос
     * @param servletResponse ответ
     * @param filterChain цепочка фильтров
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // Извлекаем данные из заголовков запроса и сохраняем в контексте
        UserContextHolder.getContext().setCorrelationId(
                httpServletRequest.getHeader(UserContext.CORRELATION_ID));
        UserContextHolder.getContext().setUserId(
                httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(
                httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setBookingId(
                httpServletRequest.getHeader(UserContext.BOOKING_ID));

        log.debug("UserContextFilter Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());

        // Продолжаем выполнение цепочки фильтров
        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}