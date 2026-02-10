package com.akuev.repository;

import com.akuev.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностями фильмов.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Находит фильмы, название которых начинается с указанной строки.
     *
     * @param startTitle начало названия фильма
     * @return список фильмов, название которых начинается с указанной строки
     */
    List<Movie> findByTitleStartingWith(String startTitle);

    /**
     * Находит фильмы по жанру.
     *
     * @param genre жанр фильма
     * @return список фильмов указанного жанра
     */
    List<Movie> findByGenre(String genre);
}