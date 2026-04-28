package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.service.StackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/stacks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Stack Controller", description = "Endpoints related to stacks")
@RequiredArgsConstructor
@ApiResponseOk
public class StackController {

    private final StackService stackService;

    @GetMapping
    public List<StackDto> findAll() {
        return stackService.findAll();
    }
}
