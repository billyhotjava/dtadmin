package com.yuzhi.dtadmin.service.mapper;

import static com.yuzhi.dtadmin.domain.ApprovalRequestAsserts.*;
import static com.yuzhi.dtadmin.domain.ApprovalRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApprovalRequestMapperTest {

    private ApprovalRequestMapper approvalRequestMapper;

    @BeforeEach
    void setUp() {
        approvalRequestMapper = new ApprovalRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApprovalRequestSample1();
        var actual = approvalRequestMapper.toEntity(approvalRequestMapper.toDto(expected));
        assertApprovalRequestAllPropertiesEquals(expected, actual);
    }
}
