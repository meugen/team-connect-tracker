package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.mapper.StackMapper;
import com.ua.teamconnect.tracker.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StackService {

    private final StackRepository stackRepository;
    private final StackMapper stackMapper;

    public List<StackDto> findAll() {
        return stackRepository.findAll()
            .stream()
            .map(stackMapper::entityToDto)
            .toList();
    }
}
