package com.akuev.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Spring для бинов, связанных с фильмами.
 */
@Configuration
public class MovieConfig {

    /**
     * Создает бин ModelMapper для преобразования объектов.
     *
     * @return экземпляр ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}