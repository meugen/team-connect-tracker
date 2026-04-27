package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.mapper.StackMapper;
import com.ua.teamconnect.tracker.repository.StackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StackService {

    private final StackRepository stackRepository;
    private final StackMapper stackMapper;

    public StackService(
        StackRepository stackRepository,
        StackMapper stackMapper
    ) {
        this.stackRepository = stackRepository;
        this.stackMapper = stackMapper;
    }

    public List<StackDto> findAll() {
        return stackRepository.findAll()
            .stream()
            .map(stackMapper::entityToDto)
            .toList();
    }
}
