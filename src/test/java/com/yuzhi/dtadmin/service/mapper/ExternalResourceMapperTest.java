package com.yuzhi.dtadmin.service.mapper;

import static com.yuzhi.dtadmin.domain.ExternalResourceAsserts.*;
import static com.yuzhi.dtadmin.domain.ExternalResourceTestSamples.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExternalResourceMapperTest {

    @Autowired
    private ExternalResourceMapper externalResourceMapper;

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExternalResourceSample1();
        var actual = externalResourceMapper.toEntity(externalResourceMapper.toDto(expected));
        assertExternalResourceAllPropertiesEquals(expected, actual);
    }
}