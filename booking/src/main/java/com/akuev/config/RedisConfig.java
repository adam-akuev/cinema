package com.akuev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Конфигурационный класс для настройки Redis.
 * Определяет сериализаторы и настройки RedisTemplate для работы с данными в Redis.
 */
@Configuration
public class RedisConfig {

    /**
     * Создает и настраивает RedisTemplate для работы с Redis.
     * Использует StringRedisSerializer для ключей и GenericJackson2JsonRedisSerializer для значений,
     * что позволяет хранить объекты в формате JSON.
     *
     * @param connectionFactory фабрика соединений с Redis
     * @return настроенный экземпляр RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Сериализатор для строковых ключей
        template.setKeySerializer(new StringRedisSerializer());

        // Сериализатор для ключей в хэшах
        template.setHashKeySerializer(new StringRedisSerializer());

        // JSON-сериализатор для значений в хэшах
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // JSON-сериализатор для обычных значений
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Отключаем поддержку транзакций (для повышения производительности)
        template.setEnableTransactionSupport(false);

        // Инициализация настроек
        template.afterPropertiesSet();

        return template;
    }
}