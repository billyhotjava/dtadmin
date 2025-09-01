package com.yuzhi.dtadmin.service.mapper;

import static com.yuzhi.dtadmin.domain.ExternalResourceAsserts.*;
import static com.yuzhi.dtadmin.domain.ExternalResourceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExternalResourceMapperTest {

    private ExternalResourceMapper externalResourceMapper;

    @BeforeEach
    void setUp() {
        externalResourceMapper = new ExternalResourceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExternalResourceSample1();
        var actual = externalResourceMapper.toEntity(externalResourceMapper.toDto(expected));
        assertExternalResourceAllPropertiesEquals(expected, actual);
    }
}
