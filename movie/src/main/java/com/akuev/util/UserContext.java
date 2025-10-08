package com.akuev.util;

import org.springframework.stereotype.Component;

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

    public static String getCorrelationId() {
        return correlationId.get();
    }

    public static void setCorrelationId(String correlationId) {
        UserContext.correlationId.set(correlationId);
    }

    public static String getAuthToken() {
        return authToken.get();
    }

    public static void setAuthToken(String authToken) {
        UserContext.authToken.set(authToken);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void setUserId(String userId) {
        UserContext.userId.set(userId);
    }

    public static String getBookingId() {
        return bookingId.get();
    }

    public static void setBookingId(String bookingId) {
        UserContext.bookingId.set(bookingId);
    }
}
