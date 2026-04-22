package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.dto.StackDto;
import com.ua.teamconnect.tracker.services.StackService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/stacks", produces = MediaType.APPLICATION_JSON_VALUE)
public class StackController {

    private final StackService stackService;

    public StackController(StackService stackService) {
        this.stackService = stackService;
    }

    @GetMapping
    public List<StackDto> findAll() {
        return stackService.findAll();
    }
}
