package com.akuev.controller;

import com.akuev.dto.MovieDTO;
import com.akuev.model.Movie;
import com.akuev.service.MovieService;
import com.akuev.util.ErrorResponse;
import com.akuev.util.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<MovieDTO> findAllMovies() {
        return movieService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Optional<MovieDTO> findMovieById(@PathVariable("id") Long id) {
        return movieService.findById(id).map(this::convertToDTO);
    }

    @GetMapping("/search/title")
    public List<MovieDTO> findMovieByTitle(@RequestParam("title") String title) {
        return movieService.findByTitle(title).stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/search/genre")
    public List<MovieDTO> findMovieByGenre(@RequestParam("genre") String genre) {
        return movieService.findByGenre(genre).stream().map(this::convertToDTO).toList();
    }

    @GetMapping("/count")
    public long countMovies() {
        return movieService.count();
    }

    @GetMapping("/exist/{id}")
    public boolean existMovieById(@PathVariable("id") Long id) {
        return movieService.existsById(id);
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
    @ResponseStatus(HttpStatus.CREATED)
    public void addMovie(@RequestBody MovieDTO movieDTO) {
        movieService.create(convertToMovie(movieDTO));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void putMovie(@PathVariable("id") Long id,
                             @RequestBody MovieDTO movieDTO) {
        Movie movie = modelMapper.map(movieDTO, Movie.class);
        movieService.update(id, movie);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteById(id);
    }

    private Movie convertToMovie(MovieDTO movieDTO) {
        return modelMapper.map(movieDTO, Movie.class);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return modelMapper.map(movie, MovieDTO.class);
    }
}
