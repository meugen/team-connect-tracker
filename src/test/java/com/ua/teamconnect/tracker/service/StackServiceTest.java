package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.StackMapper;
import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.model.entity.Stack;
import com.ua.teamconnect.tracker.repository.StackRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StackServiceTest {

    private static StackService service;
    private static StackRepository repository;
    private static StackMapper mapper;

    @BeforeAll
    static void setupService() {
        mapper = mock(StackMapper.class);
        repository = mock(StackRepository.class);
        service = new StackService(repository, mapper);
    }

    @Test
    void findAll_repositoryReturnsEntity_returnsDto() {
        var entity1 = mock(Stack.class);
        var entity2 = mock(Stack.class);
        var dto1 = mock(StackDto.class);
        var dto2 = mock(StackDto.class);

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.entityToDto(entity1)).thenReturn(dto1);
        when(mapper.entityToDto(entity2)).thenReturn(dto2);

        var result = service.findAll();
        assertEquals(List.of(dto1, dto2), result);
    }
}
