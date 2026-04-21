package com.ua.teamconnect.tracker.services;

import com.ua.teamconnect.tracker.dto.StackDto;
import com.ua.teamconnect.tracker.repositories.StackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StackService {

    private final StackRepository stackRepository;

    public StackService(StackRepository stackRepository) {
        this.stackRepository = stackRepository;
    }

    public List<StackDto> findAll() {
        return stackRepository.findAll().stream()
                .map(stack -> new StackDto(stack.getId(), stack.getName()))
                .toList();
    }
}
