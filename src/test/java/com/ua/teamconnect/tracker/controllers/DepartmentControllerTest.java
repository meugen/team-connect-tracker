package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.dto.DepartmentDto;
import com.ua.teamconnect.tracker.services.DepartmentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DepartmentControllerTest {

    private static DepartmentService service;
    private static DepartmentController controller;

    @BeforeAll
    static void setupController() {
        service = mock(DepartmentService.class);
        controller = new DepartmentController(service);
    }

    @Test
    void findAll_departmentsExists_isOk() {
        var dto1 = mock(DepartmentDto.class);
        var dto2 = mock(DepartmentDto.class);

        when(service.findAll()).thenReturn(List.of(dto1, dto2));

        var result = controller.findAll();
        assertEquals(List.of(dto1, dto2), result);
    }
}
