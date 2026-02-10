package com.akuev.controller;

import com.akuev.dto.MovieDTO;
import com.akuev.model.Movie;
import com.akuev.service.MovieService;
import com.akuev.exception.ErrorResponse;
import com.akuev.exception.MovieNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movie Controller", description = "API для управления фильмами")
@SecurityRequirement(name = "bearerAuth")
public class MovieController {
    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Получить все фильмы", description = "Возвращает список всех фильмов")
    @ApiResponse(responseCode = "200", description = "Успешно получен список фильмов")
    public ResponseEntity<List<MovieDTO>> findAllMovies() {
        List<MovieDTO> movies = movieService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Найти фильм по ID", description = "Возвращает фильм по указанному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм найден"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MovieDTO> findMovieById(@PathVariable("id") Long id) {
        return movieService.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/title")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Поиск фильмов по названию", description = "Возвращает фильмы, название которых начинается с указанной строки")
    @ApiResponse(responseCode = "200", description = "Успешно выполнено")
    public ResponseEntity<List<MovieDTO>> findMovieByTitle(@RequestParam("title") String title) {
        List<MovieDTO> movies = movieService.findByTitle(title).stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search/genre")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Поиск фильмов по жанру", description = "Возвращает фильмы указанного жанра")
    @ApiResponse(responseCode = "200", description = "Успешно выполнено")
    public ResponseEntity<List<MovieDTO>> findMovieByGenre(@RequestParam("genre") String genre) {
        List<MovieDTO> movies = movieService.findByGenre(genre).stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/count")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Получить количество фильмов", description = "Возвращает общее количество фильмов в системе")
    @ApiResponse(responseCode = "200", description = "Успешно получено количество")
    public ResponseEntity<Long> countMovies() {
        long count = movieService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exist/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Проверить существование фильма", description = "Проверяет, существует ли фильм с указанным ID")
    @ApiResponse(responseCode = "200", description = "Успешно проверено")
    public ResponseEntity<Boolean> existMovieById(@PathVariable("id") Long id) {
        boolean exists = movieService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Добавить фильм", description = "Создает новый фильм")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Фильм успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<Void> addMovie(@RequestBody MovieDTO movieDTO) {
        movieService.create(convertToMovie(movieDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Обновить фильм", description = "Обновляет существующий фильм")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<Void> putMovie(@PathVariable("id") Long id,
                                         @RequestBody MovieDTO movieDTO) {
        Movie movie = modelMapper.map(movieDTO, Movie.class);
        movieService.update(id, movie);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Удалить фильм", description = "Удаляет фильм по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Фильм успешно удален"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MovieNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Movie with this id wasn't found!",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private Movie convertToMovie(MovieDTO movieDTO) {
        return modelMapper.map(movieDTO, Movie.class);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return modelMapper.map(movie, MovieDTO.class);
    }
}