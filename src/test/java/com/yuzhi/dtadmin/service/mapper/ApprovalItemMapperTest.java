package com.yuzhi.dtadmin.service.mapper;

import static com.yuzhi.dtadmin.domain.ApprovalItemAsserts.*;
import static com.yuzhi.dtadmin.domain.ApprovalItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApprovalItemMapperTest {

    private ApprovalItemMapper approvalItemMapper;

    @BeforeEach
    void setUp() {
        approvalItemMapper = new ApprovalItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApprovalItemSample1();
        var actual = approvalItemMapper.toEntity(approvalItemMapper.toDto(expected));
        assertApprovalItemAllPropertiesEquals(expected, actual);
    }
}
