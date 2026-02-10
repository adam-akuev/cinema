package com.akuev.repository;

import com.akuev.model.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сеансами фильмов.
 */
@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {

    /**
     * Находит все сеансы для указанного фильма.
     *
     * @param movieId идентификатор фильма
     * @return список сеансов для указанного фильма
     */
    List<MovieSession> findByMovieId(Long movieId);
}