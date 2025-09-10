package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApprovalItemMapperUnitTest {

    @Autowired
    private ApprovalItemMapper approvalItemMapper;

    @Test
    void testDtoToEntityAndBack() {
        // 创建ApprovalRequest
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setId(1L);
        approvalRequest.setRequester("testUser");
        approvalRequest.setType(ApprovalType.CREATE_USER);
        approvalRequest.setCreatedAt(Instant.now());
        approvalRequest.setStatus(ApprovalStatus.PENDING);

        // 创建ApprovalItem实体
        ApprovalItem approvalItem = new ApprovalItem();
        approvalItem.setId(1L); // 设置ID
        approvalItem.setTargetKind("User");
        approvalItem.setTargetId("12345");
        approvalItem.setSeqNumber(1);
        approvalItem.setPayload("{\"name\":\"test\"}");
        approvalItem.setRequest(approvalRequest);

        // 转换为DTO
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);
        System.out.println("DTO: " + approvalItemDTO);
        assertThat(approvalItemDTO.getId()).isNotNull();
        assertThat(approvalItemDTO.getId()).isEqualTo(1L);
        assertThat(approvalItemDTO.getTargetKind()).isEqualTo("User");
        assertThat(approvalItemDTO.getTargetId()).isEqualTo("12345");
        assertThat(approvalItemDTO.getSeqNumber()).isEqualTo(1);
        assertThat(approvalItemDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
        assertThat(approvalItemDTO.getRequestId()).isEqualTo(1L);

        // 转换回实体
        ApprovalItem entityFromDTO = approvalItemMapper.toEntity(approvalItemDTO);
        System.out.println("Entity from DTO: " + entityFromDTO);
        assertThat(entityFromDTO.getId()).isNotNull();
        assertThat(entityFromDTO.getId()).isEqualTo(1L);
        assertThat(entityFromDTO.getTargetKind()).isEqualTo("User");
        assertThat(entityFromDTO.getTargetId()).isEqualTo("12345");
        assertThat(entityFromDTO.getSeqNumber()).isEqualTo(1);
        assertThat(entityFromDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
    }

    @Test
    void testDtoWithNullIdToEntityAndBack() {
        // 创建ApprovalRequest
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setId(1L);
        approvalRequest.setRequester("testUser");
        approvalRequest.setType(ApprovalType.CREATE_USER);
        approvalRequest.setCreatedAt(Instant.now());
        approvalRequest.setStatus(ApprovalStatus.PENDING);

        // 创建ApprovalItem实体，但不设置ID（模拟新实体）
        ApprovalItem approvalItemWithoutId = new ApprovalItem();
        approvalItemWithoutId.setTargetKind("User");
        approvalItemWithoutId.setTargetId("12345");
        approvalItemWithoutId.setSeqNumber(1);
        approvalItemWithoutId.setPayload("{\"name\":\"test\"}");
        approvalItemWithoutId.setRequest(approvalRequest);

        // 转换为DTO（此时DTO的ID为null）
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItemWithoutId);
        System.out.println("DTO without ID: " + approvalItemDTO);
        assertThat(approvalItemDTO.getId()).isNull();
        assertThat(approvalItemDTO.getTargetKind()).isEqualTo("User");
        assertThat(approvalItemDTO.getTargetId()).isEqualTo("12345");
        assertThat(approvalItemDTO.getSeqNumber()).isEqualTo(1);
        assertThat(approvalItemDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
        assertThat(approvalItemDTO.getRequestId()).isEqualTo(1L);

        // 转换为实体
        ApprovalItem entityFromDTO = approvalItemMapper.toEntity(approvalItemDTO);
        System.out.println("Entity from DTO without ID: " + entityFromDTO);
        assertThat(entityFromDTO.getId()).isNull(); // 实体ID应该为null
        assertThat(entityFromDTO.getTargetKind()).isEqualTo("User");
        assertThat(entityFromDTO.getTargetId()).isEqualTo("12345");
        assertThat(entityFromDTO.getSeqNumber()).isEqualTo(1);
        assertThat(entityFromDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
    }
}