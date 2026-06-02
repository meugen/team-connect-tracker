package com.ua.teamconnect.tracker.client;

import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        this.webClient = builder
            .baseUrl(baseUrl)
            .build();
        this.apiKey = apiKey;
    }

    public Mono<HolidaysList> fetchHolidaysInYear(int year) {
        return webClient.get()
            .uri("/holidays?api_key={api_key}&year={year}&country=UA", apiKey, year)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(HolidaysList.class)
            .doOnError(throwable -> {
                var message = String.format("Failed to fetch holidays for year %d", year);
                LOGGER.error(message, throwable);
            });

    }
}
