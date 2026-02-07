package com.akuev.util;

import org.springframework.stereotype.Component;

/**
 * Класс для хранения контекстной информации пользователя в рамках потока выполнения.
 * Использует ThreadLocal для хранения данных, специфичных для каждого потока.
 */
@Component
public class UserContext {
    public static final String CORRELATION_ID = "cinema-correlation-id";
    public static final String AUTH_TOKEN = "cinema-auth-token";
    public static final String USER_ID = "cinema-user-id";
    public static final String BOOKING_ID = "cinema-booking-id";

    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    private static final ThreadLocal<String> authToken = new ThreadLocal<>();
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> bookingId = new ThreadLocal<>();

    /**
     * Возвращает идентификатор корреляции для текущего потока.
     *
     * @return идентификатор корреляции или null, если не установлен
     */
    public static String getCorrelationId() {
        return correlationId.get();
    }

    /**
     * Устанавливает идентификатор корреляции для текущего потока.
     *
     * @param correlationId идентификатор корреляции
     */
    public static void setCorrelationId(String correlationId) {
        UserContext.correlationId.set(correlationId);
    }

    /**
     * Возвращает токен авторизации для текущего потока.
     *
     * @return токен авторизации или null, если не установлен
     */
    public static String getAuthToken() {
        return authToken.get();
    }

    /**
     * Устанавливает токен авторизации для текущего потока.
     *
     * @param authToken токен авторизации
     */
    public static void setAuthToken(String authToken) {
        UserContext.authToken.set(authToken);
    }

    /**
     * Возвращает идентификатор пользователя для текущего потока.
     *
     * @return идентификатор пользователя или null, если не установлен
     */
    public static String getUserId() {
        return userId.get();
    }

    /**
     * Устанавливает идентификатор пользователя для текущего потока.
     *
     * @param userId идентификатор пользователя
     */
    public static void setUserId(String userId) {
        UserContext.userId.set(userId);
    }

    /**
     * Возвращает идентификатор бронирования для текущего потока.
     *
     * @return идентификатор бронирования или null, если не установлен
     */
    public static String getBookingId() {
        return bookingId.get();
    }

    /**
     * Устанавливает идентификатор бронирования для текущего потока.
     *
     * @param bookingId идентификатор бронирования
     */
    public static void setBookingId(String bookingId) {
        UserContext.bookingId.set(bookingId);
    }
}