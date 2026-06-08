package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.PositionMapper;
import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.exception.DepartmentNotFoundException;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import com.ua.teamconnect.tracker.repository.PositionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PositionServiceTest {

    private static DepartmentRepository departmentRepository;
    private static PositionRepository positionRepository;
    private static PositionMapper positionMapper;
    private static PositionService positionService;

    @BeforeAll
    static void setupService() {
        departmentRepository = mock(DepartmentRepository.class);
        positionRepository = mock(PositionRepository.class);
        positionMapper = mock(PositionMapper.class);
        positionService = new PositionService(departmentRepository, positionRepository, positionMapper);
    }

    private Mocks prepareMocks(boolean departmentExists) {
        var entity1 = mock(Position.class);
        var entity2 = mock(Position.class);
        var entity3 = mock(Position.class);
        var dto1 = mock(PositionDto.class);
        var dto2 = mock(PositionDto.class);
        var dto3 = mock(PositionDto.class);

        when(positionRepository.findAll()).thenReturn(List.of(entity1, entity2, entity3));
        when(positionRepository.findByDepartmentId(1)).thenReturn(List.of(entity1, entity2));
        when(positionMapper.entityToDto(entity1)).thenReturn(dto1);
        when(positionMapper.entityToDto(entity2)).thenReturn(dto2);
        when(positionMapper.entityToDto(entity3)).thenReturn(dto3);
        when(departmentRepository.existsById(1)).thenReturn(departmentExists);

        return new Mocks(
            new Position[] {entity1, entity2, entity3},
            new PositionDto[] {dto1, dto2, dto3}
        );
    }

    @Test
    void findAll_departmentIdIsNull_allPositions() {
        var mocks = prepareMocks(false);

        var result = positionService.findAll(null);
        assertEquals(List.of(mocks.dtos[0], mocks.dtos[1], mocks.dtos[2]), result);
    }

    @Test
    void findAll_departmentIdIsNotNullAndExists_filteredPositions() {
        var mocks = prepareMocks(true);

        var result = positionService.findAll(1);
        assertEquals(List.of(mocks.dtos[0], mocks.dtos[1]), result);
    }

    @Test
    void findAll_departmentIdIsNotNullAndNotExists_thrownException() {
        prepareMocks(false);
        assertThrows(DepartmentNotFoundException.class,
            () -> positionService.findAll(1));
    }

    private static class Mocks {
        final Position[] entities;
        final PositionDto[] dtos;

        public Mocks(Position[] entities, PositionDto[] dtos) {
            this.entities = entities;
            this.dtos = dtos;
        }
    }
}
