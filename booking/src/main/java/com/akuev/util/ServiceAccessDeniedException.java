package com.akuev.util;

public class ServiceAccessDeniedException extends RuntimeException {
    public ServiceAccessDeniedException(String message) {
        super(message);
    }
}
