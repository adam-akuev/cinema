package com.akuev.controller;

import com.akuev.dto.MovieDTO;
import com.akuev.model.Movie;
import com.akuev.service.MovieService;
import com.akuev.exception.ErrorResponse;
import com.akuev.exception.MovieNotFoundException;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<MovieDTO>> findAllMovies() {
        List<MovieDTO> movies = movieService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<MovieDTO> findMovieById(@PathVariable("id") Long id) {
        return movieService.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/title")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<MovieDTO>> findMovieByTitle(@RequestParam("title") String title) {
        List<MovieDTO> movies = movieService.findByTitle(title).stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search/genre")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<MovieDTO>> findMovieByGenre(@RequestParam("genre") String genre) {
        List<MovieDTO> movies = movieService.findByGenre(genre).stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/count")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Long> countMovies() {
        long count = movieService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exist/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Boolean> existMovieById(@PathVariable("id") Long id) {
        boolean exists = movieService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MovieNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Movie with this id wasn't found!",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> addMovie(@RequestBody MovieDTO movieDTO) {
        movieService.create(convertToMovie(movieDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> putMovie(@PathVariable("id") Long id,
                                         @RequestBody MovieDTO movieDTO) {
        Movie movie = modelMapper.map(movieDTO, Movie.class);
        movieService.update(id, movie);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Movie convertToMovie(MovieDTO movieDTO) {
        return modelMapper.map(movieDTO, Movie.class);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return modelMapper.map(movie, MovieDTO.class);
    }
}