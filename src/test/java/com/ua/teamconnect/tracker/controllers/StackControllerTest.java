package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.model.Stack;
import com.ua.teamconnect.tracker.repositories.StackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StackControllerTest {

    @Autowired
    private StackRepository stackRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void clearTestData() {
        stackRepository.deleteAll();
    }

    @Test
    void findAll_stacksExists_isOk() {
        var javaStack = new Stack();
        javaStack.setName("Java");
        stackRepository.save(javaStack);

        buildClient(port).get()
            .uri("/stacks")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Java");
    }
}
