package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.DepartmentDto;
import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.mapper.DepartmentMapper;
import com.ua.teamconnect.tracker.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DepartmentServiceTest {

    private static DepartmentRepository repository;
    private static DepartmentMapper mapper;
    private static DepartmentService service;

    @BeforeAll
    static void setupService() {
        repository = mock(DepartmentRepository.class);
        mapper = mock(DepartmentMapper.class);
        service = new DepartmentService(repository, mapper);
    }

    @Test
    void findAll_repositoryReturnsEntity_returnsDto() {
        var entity1 = mock(Department.class);
        var entity2 = mock(Department.class);
        var dto1 = mock(DepartmentDto.class);
        var dto2 = mock(DepartmentDto.class);

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.entityToDto(entity1)).thenReturn(dto1);
        when(mapper.entityToDto(entity2)).thenReturn(dto2);

        var result = service.findAll();
        assertEquals(List.of(dto1, dto2), result);
    }
}
