package com.akuev.service;

import com.akuev.model.Movie;
import com.akuev.repository.MovieRepository;
import com.akuev.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isEmpty())
            throw new MovieNotFoundException();
        else
            return movie;
    }

    public List<Movie> findByTitle(String title) {
        return movieRepository.findByTitleStartingWith(title);
    }

    public List<Movie> findByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public boolean existsById(Long id) {
        return movieRepository.existsById(id);
    }

    public long count() {
        return movieRepository.count();
    }

    @Transactional
    public Movie create(Movie movie) {
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie update(Long id, Movie movie) {
        movie.setId(id);
        return movieRepository.save(movie);
    }

    @Transactional
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}
