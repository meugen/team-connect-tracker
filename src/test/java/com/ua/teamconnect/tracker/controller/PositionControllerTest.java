package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import com.ua.teamconnect.tracker.repository.PositionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PositionControllerTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void clearTestData() {
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private Long newPosition(String positionName, String departmentName) {
        var department = new Department();
        department.setName(departmentName);
        department = departmentRepository.save(department);
        var position = new Position();
        position.setName(positionName);
        position.setDepartment(department);
        positionRepository.save(position);
        return department.getId();
    }

    @Test
    void findAll_departmentExists_isOk() {
        var departmentId = newPosition("Java Developer", "IT");
        newPosition("HR Manager", "HR");

        buildClient(port).get()
            .uri("/positions?departmentId=" + departmentId)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Java Developer")
            .jsonPath("$[0].departmentId").isNumber();
    }

    @Test
    void findAll_departmentDoesNotExist_isNotFound() {
        newPosition("Java Developer", "IT");
        var departmentId = newPosition("HR Manager", "HR") + 1;

        var message = String.format("Department with id %d is not found", departmentId);
        buildClient(port).get()
            .uri("/positions?departmentId=" + departmentId)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo(message)
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.url").isEqualTo("/positions?departmentId=" + departmentId)
            .jsonPath("$.timestamp").exists();
    }
}
