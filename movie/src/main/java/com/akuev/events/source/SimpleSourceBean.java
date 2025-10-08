package com.akuev.events.source;

import com.akuev.events.model.MovieSessionChangeModel;
import com.akuev.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleSourceBean {
    private final StreamBridge streamBridge;

    public SimpleSourceBean(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishMovieSessionChange(ActionEnum action, Long sessionId) {
        log.debug("Sending Kafka message {} for Movie Session Id: {}", action, sessionId);

        MovieSessionChangeModel change = new MovieSessionChangeModel(
                MovieSessionChangeModel.class.getTypeName(),
                action.toString(),
                sessionId,
                UserContext.getCorrelationId()
        );

        boolean sent = streamBridge.send("movieSessionOutput-out-0", change);

        if (sent) {
            log.debug("Message sent successfully");
        } else {
            log.error("Failed to send message");
        }
    }
}
