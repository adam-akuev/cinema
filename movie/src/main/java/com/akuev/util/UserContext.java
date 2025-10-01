package com.akuev.util;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    public static final String CORRELATION_ID = "cinema-correlation-id";
    public static final String AUTH_TOKEN = "cinema-auth-token";
    public static final String USER_ID = "cinema-user-id";
    public static final String BOOKING_ID = "cinema-booking-id";

    private String correlationId = new String();
    private String authToken = new String();
    private String userId = new String();
    private String bookingId = new String();

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
