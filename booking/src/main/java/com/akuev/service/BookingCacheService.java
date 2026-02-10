package com.akuev.service;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.model.MovieSessionRedis;

import java.util.Optional;

/**
 * Сервис для кэширования информации о сеансах фильмов в Redis.
 * Обеспечивает быстрый доступ к данным сеансов для операций бронирования.
 */
public interface BookingCacheService {

    /**
     * Кэширует информацию о сеансе фильма в Redis.
     *
     * @param sessionDTO DTO с информацией о сеансе для кэширования
     */
    void cacheMovieSession(MovieSessionResponseDTO sessionDTO);

    /**
     * Получает кэшированную информацию о сеансе фильма из Redis.
     *
     * @param sessionId идентификатор сеанса
     * @return Optional с кэшированными данными сеанса или пустой, если данные не найдены
     */
    Optional<MovieSessionRedis> getCachedSession(Long sessionId);

    /**
     * Удаляет информацию о сеансе из кэша Redis.
     *
     * @param sessionId идентификатор сеанса для удаления из кэша
     */
    void evictCachedSession(Long sessionId);

    /**
     * Синхронизирует информацию о сеансе с кэшем Redis.
     * Получает актуальные данные о сеансе и сохраняет их в кэш.
     *
     * @param sessionId идентификатор сеанса для синхронизации
     */
    void syncSessionToCache(Long sessionId);
}