package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.repository.ApprovalItemRepository;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapper;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapperImpl;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ApprovalItemMapperImpl.class})
class ApprovalItemSaveProcessTest {

    @Autowired
    private ApprovalItemRepository approvalItemRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ApprovalItemMapper approvalItemMapper;

    @Test
    @Transactional
    void testFullSaveProcess() {
        // 先创建一个ApprovalRequest作为外键关联
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setRequester("testUser");
        approvalRequest.setType(ApprovalType.CREATE_USER);
        approvalRequest.setCreatedAt(Instant.now());
        approvalRequest.setStatus(ApprovalStatus.PENDING);
        approvalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        // 创建ApprovalItem实体（没有ID）
        ApprovalItem approvalItemWithoutId = new ApprovalItem();
        approvalItemWithoutId.setTargetKind("User");
        approvalItemWithoutId.setTargetId("12345");
        approvalItemWithoutId.setSeqNumber(1);
        approvalItemWithoutId.setPayload("{\"name\":\"test\"}");
        approvalItemWithoutId.setRequest(approvalRequest);

        System.out.println("Original entity (no ID): " + approvalItemWithoutId);

        // 转换为DTO（此时DTO的ID为null）
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItemWithoutId);
        System.out.println("DTO before save (ID should be null): " + approvalItemDTO);
        assertThat(approvalItemDTO.getId()).isNull();

        // 模拟ApprovalItemService.save()的流程
        // 1. 将DTO转换为实体
        ApprovalItem entityToSave = approvalItemMapper.toEntity(approvalItemDTO);
        System.out.println("Entity to save (ID should be null): " + entityToSave);
        assertThat(entityToSave.getId()).isNull();

        // 2. 保存实体到数据库
        ApprovalItem savedEntity = approvalItemRepository.saveAndFlush(entityToSave);
        System.out.println("Entity after save (ID should NOT be null): " + savedEntity);
        assertThat(savedEntity.getId()).isNotNull();

        // 3. 将保存后的实体转换为DTO
        ApprovalItemDTO savedDTO = approvalItemMapper.toDto(savedEntity);
        System.out.println("DTO after save (ID should NOT be null): " + savedDTO);
        assertThat(savedDTO.getId()).isNotNull();
        assertThat(savedDTO.getId()).isEqualTo(savedEntity.getId());
    }
}