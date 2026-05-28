package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.entity.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class PositionMapperTest {

    private static PositionMapper mapper;

    @BeforeAll
    static void setupMapper() {
        mapper = Mappers.getMapper(PositionMapper.class);
    }

    @Test
    void entityToDto_entityIsNull_dtoIsNull() {
        var dto = mapper.entityToDto(null);
        assertNull(dto);
    }

    @Test
    void entityToDto_entityIsNotNull_validDto() {
        var entity = new Position();
        entity.setId(1);
        entity.setName("Backend Developer");
        entity.setDepartmentId(2);
        var dto = mapper.entityToDto(entity);

        assertNotNull(dto);
        assertEquals(1, dto.id());
        assertEquals("Backend Developer", dto.name());
        assertEquals(2, dto.departmentId());
    }
}
