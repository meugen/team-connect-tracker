package com.ua.teamconnect.tracker.services;

import com.ua.teamconnect.tracker.dto.PositionDto;
import com.ua.teamconnect.tracker.exceptions.DepartmentNotFoundException;
import com.ua.teamconnect.tracker.mappers.PositionMapper;
import com.ua.teamconnect.tracker.model.Position;
import com.ua.teamconnect.tracker.repositories.DepartmentRepository;
import com.ua.teamconnect.tracker.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionService(
        DepartmentRepository departmentRepository,
        PositionRepository positionRepository,
        PositionMapper positionMapper
    ) {
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    public List<PositionDto> findAll(Optional<Long> departmentId) {
        return departmentId
            .map(this::findAllByDepartmentId)
            .orElseGet(this::findAll)
            .stream()
            .map(positionMapper::entityToDto)
            .toList();
    }

    private List<Position> findAll() {
        return positionRepository.findAll();
    }

    private List<Position> findAllByDepartmentId(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return positionRepository.findByDepartmentId(departmentId);
    }
}
