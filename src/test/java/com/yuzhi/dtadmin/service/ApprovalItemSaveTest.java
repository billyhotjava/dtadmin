package com.yuzhi.dtadmin.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.IntegrationTest;
import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.repository.ApprovalItemRepository;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@Transactional
class ApprovalItemSaveTest {

    @Autowired
    private ApprovalItemService approvalItemService;

    @Autowired
    private ApprovalItemRepository approvalItemRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ApprovalItemMapper approvalItemMapper;

    @Test
    void testSaveApprovalItem() {
        // 先创建一个ApprovalRequest作为外键关联
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setId(1L);
        approvalRequest.setRequester("testUser");
        approvalRequest.setType(ApprovalType.CREATE_USER);
        approvalRequest.setCreatedAt(Instant.now());
        approvalRequest.setStatus(ApprovalStatus.PENDING);
        approvalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        // 创建ApprovalItem
        ApprovalItem approvalItem = new ApprovalItem();
        approvalItem.setTargetKind("User");
        approvalItem.setTargetId("12345");
        approvalItem.setSeqNumber(1);
        approvalItem.setPayload("{\"name\":\"test\"}");
        approvalItem.setRequestId(approvalRequest.getId());

        // 转换为DTO并保存
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);
        System.out.println("Before save - DTO ID: " + approvalItemDTO.getId());
        
        ApprovalItemDTO savedApprovalItemDTO = approvalItemService.save(approvalItemDTO);
        
        System.out.println("After save - DTO ID: " + savedApprovalItemDTO.getId());
        System.out.println("After save - DTO: " + savedApprovalItemDTO);

        // 验证保存成功
        assertThat(savedApprovalItemDTO.getId()).isNotNull();
        assertThat(savedApprovalItemDTO.getTargetKind()).isEqualTo("User");
        assertThat(savedApprovalItemDTO.getTargetId()).isEqualTo("12345");
        assertThat(savedApprovalItemDTO.getSeqNumber()).isEqualTo(1);
        assertThat(savedApprovalItemDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
        assertThat(savedApprovalItemDTO.getRequestId()).isEqualTo(approvalRequest.getId());
    }
}