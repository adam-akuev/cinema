package com.akuev.controller;

import com.akuev.dto.MovieSessionDTO;
import com.akuev.model.Movie;
import com.akuev.model.MovieSession;
import com.akuev.service.MovieService;
import com.akuev.service.MovieSessionService;
import com.akuev.exception.ErrorResponse;
import com.akuev.exception.MovieSessionNotFoundException;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/movie-sessions")
@RequiredArgsConstructor
public class MovieSessionController {
    private final MovieSessionService movieSessionService;
    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"USER", "ADMIN"})
    public List<MovieSessionDTO> findAll() {
        return movieSessionService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Optional<MovieSessionDTO> findSessionById(@PathVariable("id") Long id) {
        return movieSessionService.findById(id).map(this::convertToDTO);
    }

    @GetMapping("/exist/{id}")
    @RolesAllowed({"ADMIN"})
    public boolean existMovieById(@PathVariable("id") Long id) {
        return movieSessionService.existsById(id);
    }

    @GetMapping("/{movieId}/sessions")
    @RolesAllowed({"USER", "ADMIN"})
    public List<MovieSessionDTO> findSessionsByMovieId(@PathVariable("movieId") Long id) {
        return movieSessionService.findMovieSessions(id).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"ADMIN"})
    public void addSession(@RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.create(session);
    }

    /*@PostMapping("/movie/{movieId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSessionByMovieId(@PathVariable("movieId") Long movieId,
                                    @RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.createForMovie(movieId, session);
    }*/

    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public void putMovieSession(@PathVariable("id") Long id,
                                @RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.update(id, session);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed({"ADMIN"})
    public void deleteById(@PathVariable("id") Long id) {
        movieSessionService.deleteById(id);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MovieSessionNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Movie session with this id wasn't found!",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private MovieSessionDTO convertToDTO(MovieSession movieSession) {
        return modelMapper.map(movieSession, MovieSessionDTO.class);
    }

    private MovieSession convertToMovieSession(MovieSessionDTO movieSessionDTO) {
        MovieSession session = new MovieSession();
        session.setStartTime(movieSessionDTO.getStartTime());
        session.setHallNumber(movieSessionDTO.getHallNumber());
        session.setPrice(movieSessionDTO.getPrice());

        Movie movie = movieService.findById(movieSessionDTO.getMovieId()).get();
        session.setMovie(movie);
        return session;
    }
}
