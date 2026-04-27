package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.model.entity.Stack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class StackMapperTest {

    private static StackMapper mapper;

    @BeforeAll
    static void setupMapper() {
        mapper = Mappers.getMapper(StackMapper.class);
    }

    @Test
    void entityToDto_entityIsNull_dtoIsNull() {
        var dto = mapper.entityToDto(null);
        assertNull(dto);
    }

    @Test
    void entityToDto_entityIsNotNull_valiDto() {
        Stack entity = new Stack();
        entity.setId(1L);
        entity.setName("test");
        StackDto dto = mapper.entityToDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("test", dto.name());
    }
}
