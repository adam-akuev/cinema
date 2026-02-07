package com.akuev.service;

import java.util.UUID;

/**
 * Сервис безопасности для работы с аутентификацией и авторизацией.
 */
public interface SecurityService {
    /**
     * Возвращает ID текущего аутентифицированного пользователя.
     * @throws SecurityException если пользователь не аутентифицирован
     */
    UUID getCurrentUserId();

    /**
     * Проверяет, имеет ли текущий пользователь роль администратора.
     */
    boolean hasAdminRole();
}