package com.akuev.exception;

/**
 * Исключение, выбрасываемое когда пользователь не найден.
 * Используется в сервисном слое приложения.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Создает исключение с сообщением об ошибке.
     *
     * @param message описание ошибки
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}