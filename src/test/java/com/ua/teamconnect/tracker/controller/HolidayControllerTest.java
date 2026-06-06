package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Holiday;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HolidayControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private HolidayRepository holidayRepository;

    @AfterEach
    void cleanUp() {
        holidayRepository.deleteAll();
    }

    private void setupData() {
        var holiday1 = new Holiday();
        holiday1.setId("holiday-1");
        holiday1.setName("Holiday 1");
        holiday1.setDescription("Holiday 1 description");
        holiday1.setDate(LocalDate.now().minusDays(2));
        holidayRepository.save(holiday1);

        var holiday2 = new Holiday();
        holiday2.setId("holiday-2");
        holiday2.setName("Holiday 2");
        holiday2.setDescription("Holiday 2 description");
        holiday2.setDate(LocalDate.now().plusDays(2));
        holidayRepository.save(holiday2);
    }


    @Test
    void findFiveUpcoming_valid_response() {
        setupData();
        setupValidToken();

        var expectedDate = LocalDate.now().plusDays(2);
        buildClient(port).get()
            .uri("/holidays/upcoming")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isEqualTo("holiday-2")
            .jsonPath("$[0].name").isEqualTo("Holiday 2")
            .jsonPath("$[0].description").isEqualTo("Holiday 2 description")
            .jsonPath("$[0].date").isEqualTo(expectedDate.toString())
            .jsonPath("$[0].dayOfWeek").isEqualTo(expectedDate.getDayOfWeek().toString());
    }

    @Test
    void findFiveUpcoming_invalidToken_unauthorized() {
        setupData();
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/holidays/upcoming")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }
}
