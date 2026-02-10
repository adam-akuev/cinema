package com.akuev.service.impl;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.exception.SessionNotFoundException;
import com.akuev.model.MovieSessionRedis;
import com.akuev.repository.MovieSessionRedisRepository;
import com.akuev.service.BookingCacheService;
import com.akuev.service.client.MovieFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Реализация сервиса для кэширования информации о сеансах фильмов в Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCacheServiceImpl implements BookingCacheService {
    private final MovieSessionRedisRepository movieSessionRedisRepository;
    private final MovieFeignClient movieFeignClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public void cacheMovieSession(MovieSessionResponseDTO sessionDTO) {
        try {
            MovieSessionRedis sessionRedis = new MovieSessionRedis(sessionDTO);
            movieSessionRedisRepository.save(sessionRedis);
            log.debug("Cached session in Redis: {}", sessionDTO.getId());
        } catch (Exception e) {
            log.error("Failed to cache session: {}", sessionDTO.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MovieSessionRedis> getCachedSession(Long sessionId) {
        try {
            return movieSessionRedisRepository.findById(sessionId);
        } catch (Exception e) {
            log.error("Failed to get cached session: {}", sessionId);
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictCachedSession(Long sessionId) {
        try {
            movieSessionRedisRepository.deleteById(sessionId);
            log.debug("Evicted session from Redis: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to evict session: {}", sessionId, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncSessionToCache(Long sessionId) {
        try {
            MovieSessionResponseDTO sessionDTO = movieFeignClient.findSessionById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException("Session by id " + sessionId + " not found!"));
            cacheMovieSession(sessionDTO);
            log.debug("Synced session to Redis cache: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to sync session to cache: {}", sessionId, e);
        }
    }
}