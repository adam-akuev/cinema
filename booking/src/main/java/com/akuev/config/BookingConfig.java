package com.akuev.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BookingConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
