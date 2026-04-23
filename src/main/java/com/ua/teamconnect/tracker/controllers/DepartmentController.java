package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.dto.DepartmentDto;
import com.ua.teamconnect.tracker.services.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Department Controller", description = "Endpoints related to departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<DepartmentDto> findAll() {
        return departmentService.findAll();
    }
}
