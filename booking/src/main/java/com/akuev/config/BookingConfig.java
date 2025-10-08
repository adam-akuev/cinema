package com.akuev.config;

import com.akuev.event.model.ActionEnum;
import com.akuev.event.model.MovieSessionChangeModel;
import com.akuev.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class BookingConfig {

    private final BookingService bookingService;

    public BookingConfig(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Consumer<MovieSessionChangeModel> input() {
        return orgChange -> {
            log.info("Received an {} event for Movie Session Id {}", orgChange.getAction(), orgChange.getSessionId());

            try {
                switch (ActionEnum.valueOf(orgChange.getAction())) {
                    case CREATED:
                        bookingService.syncSessionToCache(orgChange.getSessionId());
                        log.info("Cached new session: {}", orgChange.getSessionId());
                        break;
                    case UPDATED:
                        bookingService.syncSessionToCache(orgChange.getSessionId());
                        log.info("Refreshed cache for updated session: {}", orgChange.getSessionId());
                        break;
                    case DELETED:
                        bookingService.evictCachedSession(orgChange.getSessionId());
                        log.info("Evicted deleted session from cache: {}", orgChange.getSessionId());
                        break;
                }
            } catch (Exception e) {
                log.error("Error processing session change: {}", e.getMessage());
            }
        };
    }

}
