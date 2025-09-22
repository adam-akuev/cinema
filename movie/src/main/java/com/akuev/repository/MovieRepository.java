package com.akuev.repository;

import com.akuev.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleStartingWith(String startTitle);
    List<Movie> findByGenre(String genre);

}
