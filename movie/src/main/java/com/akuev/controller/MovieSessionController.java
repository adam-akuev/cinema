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
    public ResponseEntity<List<MovieSessionDTO>> findAll() {
        List<MovieSessionDTO> sessions = movieSessionService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<MovieSessionDTO> findSessionById(@PathVariable("id") Long id) {
        return movieSessionService.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exist/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Boolean> existMovieById(@PathVariable("id") Long id) {
        boolean exists = movieSessionService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{movieId}/sessions")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<MovieSessionDTO>> findSessionsByMovieId(@PathVariable("movieId") Long id) {
        List<MovieSessionDTO> sessions = movieSessionService.findMovieSessions(id).stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> addSession(@RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.create(session);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*@PostMapping("/movie/{movieId}")
    public ResponseEntity<Void> addSessionByMovieId(@PathVariable("movieId") Long movieId,
                                    @RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.createForMovie(movieId, session);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/

    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> putMovieSession(@PathVariable("id") Long id,
                                                @RequestBody MovieSessionDTO sessionDTO) {
        MovieSession session = convertToMovieSession(sessionDTO);
        movieSessionService.update(id, session);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        movieSessionService.deleteById(id);
        return ResponseEntity.noContent().build();
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