package com.ua.teamconnect.tracker.client;

import com.ua.teamconnect.tracker.model.entity.Holiday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class HolidayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayClient.class);

    private final WebClient webClient;
    private final String apiKey;

    public HolidayClient(
        WebClient.Builder builder,
        @Value("${team.connect.calendarific.api-key}")
        String apiKey,
        @Value("${team.connect.calendarific.base-url}")
        String baseUrl
    ) {
        var objectMapper = Jackson2ObjectMapperBuilder.json()
            .deserializerByType(HolidaysDeserializer.HolidaysList.class, new HolidaysDeserializer())
            .build();
        this.webClient = builder
            .baseUrl(baseUrl)
            .codecs(configurer -> configurer
                .defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper)))
            .build();
        this.apiKey = apiKey;
    }

    public Mono<Set<Holiday>> fetchHolidaysInYear(int year) {
        return webClient.get()
            .uri("/holidays?api_key={api_key}&year={year}&country=UA", apiKey, year)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(HolidaysDeserializer.HolidaysList.class)
            .map(HolidaysDeserializer.HolidaysList::holidays)
            .doOnError(throwable -> {
                var message = String.format("Failed to fetch holidays for year %d", year);
                LOGGER.error(message, throwable);
            });

    }
}
