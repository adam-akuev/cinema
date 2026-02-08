package com.akuev.util;

/**
 * Хранитель контекста пользователя, использующий ThreadLocal для хранения данных,
 * специфичных для каждого потока выполнения.
 * Обеспечивает доступ к контексту пользователя в рамках одного HTTP-запроса.
 */
public class UserContextHolder {

    /**
     * ThreadLocal для хранения контекста пользователя.
     * Гарантирует изоляцию данных между потоками выполнения.
     */
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    /**
     * Возвращает контекст пользователя для текущего потока.
     * Если контекст не был установлен, создается и устанавливается новый пустой контекст.
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
     * @return новый пустой экземпляр UserContext
     */
    public static final UserContext createEmptyContext() {
        return new UserContext();
    }
}