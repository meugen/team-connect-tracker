package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.DepartmentDto;
import com.ua.teamconnect.tracker.mapper.DepartmentMapper;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll()
            .stream()
            .map(departmentMapper::entityToDto)
            .toList();
    }
}
