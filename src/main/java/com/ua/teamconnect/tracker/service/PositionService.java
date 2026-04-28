package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.PositionMapper;
import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.exception.DepartmentNotFoundException;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import com.ua.teamconnect.tracker.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public List<PositionDto> findAll(Long departmentId) {
        var positions = departmentId == null ? findAll() : findAllByDepartmentId(departmentId);
        return positions.stream()
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
