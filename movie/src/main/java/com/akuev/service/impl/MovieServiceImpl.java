package com.akuev.service.impl;

import com.akuev.model.Movie;
import com.akuev.repository.MovieRepository;
import com.akuev.exception.MovieNotFoundException;
import com.akuev.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с фильмами.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Movie> findById(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isEmpty())
            throw new MovieNotFoundException();
        else
            return movie;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movie> findByTitle(String title) {
        return movieRepository.findByTitleStartingWith(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movie> findByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return movieRepository.existsById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return movieRepository.count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Movie create(Movie movie) {
        return movieRepository.save(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Movie update(Long id, Movie movie) {
        movie.setId(id);
        return movieRepository.save(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}