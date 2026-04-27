package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.DepartmentDto;
import com.ua.teamconnect.tracker.mapper.DepartmentMapper;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(
        DepartmentRepository departmentRepository,
        DepartmentMapper departmentMapper
    ) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll()
            .stream()
            .map(departmentMapper::entityToDto)
            .toList();
    }
}
