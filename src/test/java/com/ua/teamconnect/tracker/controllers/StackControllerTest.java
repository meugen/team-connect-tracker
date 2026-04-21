package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.model.Stack;
import com.ua.teamconnect.tracker.repositories.StackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

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

    private Stack newStack(String name) {
        var stack = new Stack();
        stack.setName(name);
        return stack;
    }

    private void validateStackItem(WebTestClient.BodyContentSpec spec, int index, String name) {
        var prefix = String.format("$[%d]", index);
        spec.jsonPath(prefix + ".id").isNumber()
            .jsonPath(prefix + ".name").isEqualTo(name);
    }

    @Test
    void findAll_stacksExists_isOk() {
        stackRepository.saveAll(List.of(
            newStack("Java"), newStack("Python"),
            newStack("Ruby"), newStack("JavaScript")
        ));

        var spec = buildClient(port).get()
            .uri("/stacks")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(4);
        validateStackItem(spec, 0, "Java");
        validateStackItem(spec, 1, "Python");
        validateStackItem(spec, 2, "Ruby");
        validateStackItem(spec, 3, "JavaScript");
    }
}
