package com.akuev.exception;

public class BookingFailedException extends RuntimeException {
    public BookingFailedException(String message) {
        super(message);
    }
}

