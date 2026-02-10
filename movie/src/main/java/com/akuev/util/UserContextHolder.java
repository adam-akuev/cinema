package com.akuev.util;

/**
 * Хранитель контекста пользователя для текущего потока выполнения.
 * Использует ThreadLocal для хранения экземпляров UserContext.
 */
public class UserContextHolder {

    /**
     * Хранилище контекста пользователя для текущего потока.
     */
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    /**
     * Возвращает контекст пользователя для текущего потока.
     * Если контекст не установлен, создается и устанавливается пустой контекст.
     *
     * @return контекст пользователя для текущего потока
     */
    public static final UserContext getContext() {
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);
        }
        return userContext.get();
    }

    /**
     * Устанавливает контекст пользователя для текущего потока.
     *
     * @param context контекст пользователя для установки
     */
    public static final void setContext(UserContext context) {
        userContext.set(context);
    }

    /**
     * Создает новый пустой контекст пользователя.
     *
     * @return новый пустой контекст пользователя
     */
    public static final UserContext createEmptyContext() {
        return new UserContext();
    }
}