package com.akuev.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки ModelMapper.
 */
@Configuration
public class UserConfig {

    /**
     * Создает и возвращает экземпляр ModelMapper.
     * ModelMapper используется для преобразования объектов между слоями приложения.
     *
     * @return экземпляр ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}