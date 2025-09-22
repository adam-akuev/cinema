package com.akuev.repository;

import com.akuev.model.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {
    List<MovieSession> findByMovieId(Long movieId);
}
