package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepartmentControllerTest extends AuthorizationControllerTest {

    @Autowired
    private DepartmentRepository repository;

    @LocalServerPort
    private int port;

    @AfterEach
    void clearTestData() {
        repository.deleteAll();
    }

    private Department newDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        department.setHeadId(1L);
        return department;
    }

    private void validateDepartmentItem(WebTestClient.BodyContentSpec spec, int index, String name) {
        var prefix = String.format("$[%d]", index);
        spec.jsonPath(prefix + ".id").isNumber()
            .jsonPath(prefix + ".name").isEqualTo(name)
            .jsonPath(prefix + ".headId").isNumber();
    }

    @Test
    void findAll_departmentsExists_isOk() {
        repository.saveAll(List.of(
            newDepartment("Software Development"),
            newDepartment("HR Department")
        ));

        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/departments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(2);
        validateDepartmentItem(spec, 0, "Software Development");
        validateDepartmentItem(spec, 1, "HR Department");
    }

    @Test
    void findAll_invalidToken_isUnauthorized() {
        repository.saveAll(List.of(
            newDepartment("Software Development"),
            newDepartment("HR Department")
        ));

        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/departments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }
}
