package com.akuev.config;

import com.akuev.event.model.ActionEnum;
import com.akuev.event.model.MovieSessionChangeModel;
import com.akuev.service.BookingCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Конфигурационный класс для сервиса бронирования.
 * Настраивает обработку событий изменений сеансов фильмов из Kafka.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BookingConfig {
    private final BookingCacheService bookingCacheService;

    /**
     * Создает и настраивает экземпляр ModelMapper для преобразования объектов.
     *
     * @return настроенный экземпляр ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Создает consumer для обработки событий изменений сеансов фильмов из Kafka.
     * События обрабатываются в соответствии с типом действия (создание, обновление, удаление).
     *
     * @return consumer для обработки событий MovieSessionChangeModel
     */
    @Bean
    public Consumer<MovieSessionChangeModel> input() {
        return orgChange -> {
            log.info("Received an {} event for Movie Session Id {}", orgChange.getAction(), orgChange.getSessionId());

            try {
                switch (ActionEnum.valueOf(orgChange.getAction())) {
                    case CREATED:
                        // Синхронизация нового сеанса в кэше
                        bookingCacheService.syncSessionToCache(orgChange.getSessionId());
                        log.info("Cached new session: {}", orgChange.getSessionId());
                        break;
                    case UPDATED:
                        // Обновление кэша для измененного сеанса
                        bookingCacheService.syncSessionToCache(orgChange.getSessionId());
                        log.info("Refreshed cache for updated session: {}", orgChange.getSessionId());
                        break;
                    case DELETED:
                        // Удаление сеанса из кэша
                        bookingCacheService.evictCachedSession(orgChange.getSessionId());
                        log.info("Evicted deleted session from cache: {}", orgChange.getSessionId());
                        break;
                }
            } catch (Exception e) {
                log.error("Error processing session change: {}", e.getMessage());
            }
        };
    }
}