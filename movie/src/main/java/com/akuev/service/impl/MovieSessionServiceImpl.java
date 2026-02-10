package com.akuev.service.impl;

import com.akuev.events.source.ActionEnum;
import com.akuev.events.source.SimpleSourceBean;
import com.akuev.model.Movie;
import com.akuev.model.MovieSession;
import com.akuev.repository.MovieSessionRepository;
import com.akuev.exception.MovieSessionNotFoundException;
import com.akuev.service.MovieService;
import com.akuev.service.MovieSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация сервиса для работы с сеансами фильмов.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieSessionServiceImpl implements MovieSessionService {
    private final MovieSessionRepository movieSessionRepository;
    private final MovieService movieService;
    private final SimpleSourceBean simpleSourceBean;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MovieSession> findAll() {
        return movieSessionRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MovieSession> findById(Long id) {
        Optional<MovieSession> session = movieSessionRepository.findById(id);

        if (session.isEmpty())
            throw new MovieSessionNotFoundException();
        else
            return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MovieSession> findMovieSessions(Long id) {
        return movieSessionRepository.findByMovieId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return movieSessionRepository.existsById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void create(MovieSession session) {
        movieSessionRepository.save(session);
        simpleSourceBean.publishMovieSessionChange(ActionEnum.CREATED, session.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieSession createForMovie(Long movieId, MovieSession session) {
        Optional<Movie> movie = movieService.findById(movieId);
        session.setMovie(movie.get());
        MovieSession createdSession = movieSessionRepository.save(session);
        simpleSourceBean.publishMovieSessionChange(ActionEnum.CREATED, session.getId());
        return createdSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieSession update(Long id, MovieSession session) {
        session.setId(id);
        MovieSession updatedMovie = movieSessionRepository.save(session);
        simpleSourceBean.publishMovieSessionChange(ActionEnum.UPDATED, session.getId());
        return updatedMovie;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean bookingSeats(Long sessionId, Set<String> seatsForBooking) {
        MovieSession session = findById(sessionId).get();

        if (session.getAvailableSeats() == null) {
            session.setAvailableSeats(new HashSet<>());
        }
        if (session.getBookedSeats() == null) {
            session.setBookedSeats(new HashSet<>());
        }

        if (session.getAvailableSeats().containsAll(seatsForBooking)) {
            session.getAvailableSeats().removeAll(seatsForBooking);
            session.getBookedSeats().addAll(seatsForBooking);
            movieSessionRepository.save(session);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void freeBookingSeats(Long sessionId, Set<String> seatsForFree) {
        MovieSession session = findById(sessionId).get();

        if (session.getAvailableSeats() == null) {
            session.setAvailableSeats(new HashSet<>());
        }
        if (session.getBookedSeats() == null) {
            session.setBookedSeats(new HashSet<>());
        }

        session.getBookedSeats().removeAll(seatsForFree);
        session.getAvailableSeats().addAll(seatsForFree);
        movieSessionRepository.save(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        movieSessionRepository.deleteById(id);
        simpleSourceBean.publishMovieSessionChange(ActionEnum.DELETED, id);
    }
}