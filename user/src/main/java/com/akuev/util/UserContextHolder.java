package com.akuev.util;

/**
 * Хранилище для {@link UserContext} с привязкой к текущему потоку выполнения.
 * Обеспечивает изоляцию контекста пользователя между разными запросами.
 */
public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    /**
     * Возвращает контекст пользователя для текущего потока.
     * Если контекст не установлен, создает и сохраняет пустой.
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
     */
    public static final void setContext(UserContext context) {
        userContext.set(context);
    }

    /**
     * Создает новый пустой экземпляр {@link UserContext}.
     */
    public static final UserContext createEmptyContext() {
        return new UserContext();
    }
}