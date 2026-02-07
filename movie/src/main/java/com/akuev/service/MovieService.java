package com.akuev.service;

import com.akuev.model.Movie;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с фильмами.
 */
public interface MovieService {

    /**
     * Получает список всех фильмов.
     *
     * @return список всех фильмов
     */
    List<Movie> findAll();

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return Optional с найденным фильмом или пустой, если фильм не найден
     */
    Optional<Movie> findById(Long id);

    /**
     * Находит фильмы по названию (поиск по началу названия).
     *
     * @param title начало названия фильма для поиска
     * @return список фильмов, название которых начинается с указанной строки
     */
    List<Movie> findByTitle(String title);

    /**
     * Находит фильмы по жанру.
     *
     * @param genre жанр фильма
     * @return список фильмов указанного жанра
     */
    List<Movie> findByGenre(String genre);

    /**
     * Проверяет существование фильма по идентификатору.
     *
     * @param id идентификатор фильма
     * @return true, если фильм существует, false - в противном случае
     */
    boolean existsById(Long id);

    /**
     * Возвращает общее количество фильмов.
     *
     * @return количество фильмов
     */
    long count();

    /**
     * Создает новый фильм.
     *
     * @param movie фильм для создания
     * @return созданный фильм
     */
    Movie create(Movie movie);

    /**
     * Обновляет существующий фильм.
     *
     * @param id идентификатор фильма для обновления
     * @param movie данные фильма для обновления
     * @return обновленный фильм
     */
    Movie update(Long id, Movie movie);

    /**
     * Удаляет фильм по идентификатору.
     *
     * @param id идентификатор фильма для удаления
     */
    void deleteById(Long id);
}