package com.ua.teamconnect.tracker.mappers;

import com.ua.teamconnect.tracker.model.Department;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentMapperTest {

    private static DepartmentMapper mapper;

    @BeforeAll
    static void setupMapper() {
        mapper = Mappers.getMapper(DepartmentMapper.class);
    }

    @Test
    void entityToDto_entityIsNull_dtoIsNull() {
        var dto = mapper.entityToDto(null);
        assertNull(dto);
    }

    @Test
    void entityToDto_entityIsNotNull_validDto() {
        var entity = new Department();
        entity.setId(1L);
        entity.setName("Software Development");
        var dto = mapper.entityToDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Software Development", dto.name());
        assertEquals(0L, dto.headId());
    }
}
