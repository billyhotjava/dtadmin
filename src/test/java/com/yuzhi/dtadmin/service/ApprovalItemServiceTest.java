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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@Transactional
class ApprovalItemServiceTest {

    @Autowired
    private ApprovalItemService approvalItemService;

    @Autowired
    private ApprovalItemRepository approvalItemRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ApprovalItemMapper approvalItemMapper;

    private ApprovalRequest approvalRequest;
    private ApprovalItem approvalItem;
    private ApprovalItemDTO approvalItemDTO;

    @BeforeEach
    public void init() {
        // 先创建一个ApprovalRequest作为外键关联
        approvalRequest = new ApprovalRequest();
        approvalRequest.setId(1L);
        approvalRequest.setRequester("testUser");
        approvalRequest.setType(ApprovalType.CREATE_USER);
        approvalRequest.setCreatedAt(Instant.now());
        approvalRequest.setStatus(ApprovalStatus.PENDING);
        approvalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        // 创建ApprovalItem
        approvalItem = new ApprovalItem();
        approvalItem.setTargetKind("User");
        approvalItem.setTargetId("12345");
        approvalItem.setSeqNumber(1);
        approvalItem.setPayload("{\"name\":\"test\"}");
        approvalItem.setRequest(approvalRequest);

        // 创建ApprovalItemDTO
        approvalItemDTO = approvalItemMapper.toDto(approvalItem);
    }

    @Test
    void saveApprovalItem() {
        // 保存ApprovalItemDTO
        ApprovalItemDTO savedApprovalItemDTO = approvalItemService.save(approvalItemDTO);

        // 验证保存成功
        assertThat(savedApprovalItemDTO.getId()).isNotNull();
        assertThat(savedApprovalItemDTO.getTargetKind()).isEqualTo("User");
        assertThat(savedApprovalItemDTO.getTargetId()).isEqualTo("12345");
        assertThat(savedApprovalItemDTO.getSeqNumber()).isEqualTo(1);
        assertThat(savedApprovalItemDTO.getPayload()).isEqualTo("{\"name\":\"test\"}");
        assertThat(savedApprovalItemDTO.getRequestId()).isEqualTo(approvalRequest.getId());

        // 从数据库中查询验证
        Optional<ApprovalItem> foundItem = approvalItemRepository.findById(savedApprovalItemDTO.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.orElseThrow().getTargetKind()).isEqualTo("User");
        assertThat(foundItem.orElseThrow().getTargetId()).isEqualTo("12345");
        assertThat(foundItem.orElseThrow().getSeqNumber()).isEqualTo(1);
        assertThat(foundItem.orElseThrow().getPayload()).isEqualTo("{\"name\":\"test\"}");
        assertThat(foundItem.orElseThrow().getRequest()).isEqualTo(approvalRequest);
    }

    @Test
    void updateApprovalItem() {
        // 先保存一个ApprovalItem
        ApprovalItemDTO savedApprovalItemDTO = approvalItemService.save(approvalItemDTO);

        // 更新数据
        savedApprovalItemDTO.setTargetKind("UpdatedUser");
        savedApprovalItemDTO.setTargetId("67890");
        savedApprovalItemDTO.setSeqNumber(2);
        savedApprovalItemDTO.setPayload("{\"name\":\"updated\"}");

        // 执行更新
        ApprovalItemDTO updatedApprovalItemDTO = approvalItemService.update(savedApprovalItemDTO);

        // 验证更新成功
        assertThat(updatedApprovalItemDTO.getId()).isEqualTo(savedApprovalItemDTO.getId());
        assertThat(updatedApprovalItemDTO.getTargetKind()).isEqualTo("UpdatedUser");
        assertThat(updatedApprovalItemDTO.getTargetId()).isEqualTo("67890");
        assertThat(updatedApprovalItemDTO.getSeqNumber()).isEqualTo(2);
        assertThat(updatedApprovalItemDTO.getPayload()).isEqualTo("{\"name\":\"updated\"}");

        // 从数据库中查询验证
        Optional<ApprovalItem> foundItem = approvalItemRepository.findById(updatedApprovalItemDTO.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.orElseThrow().getTargetKind()).isEqualTo("UpdatedUser");
        assertThat(foundItem.orElseThrow().getTargetId()).isEqualTo("67890");
        assertThat(foundItem.orElseThrow().getSeqNumber()).isEqualTo(2);
        assertThat(foundItem.orElseThrow().getPayload()).isEqualTo("{\"name\":\"updated\"}");
    }

    @Test
    void findAllApprovalItems() {
        // 保存多个ApprovalItem
        approvalItemService.save(approvalItemDTO);

        ApprovalItem anotherItem = new ApprovalItem();
        anotherItem.setTargetKind("Role");
        anotherItem.setTargetId("role123");
        anotherItem.setSeqNumber(2);
        anotherItem.setPayload("{\"role\":\"admin\"}");
        anotherItem.setRequest(approvalRequest);
        ApprovalItemDTO anotherItemDTO = approvalItemMapper.toDto(anotherItem);
        approvalItemService.save(anotherItemDTO);

        // 查询所有ApprovalItem
        List<ApprovalItem> allItems = approvalItemRepository.findAll();
        assertThat(allItems).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteApprovalItem() {
        // 保存一个ApprovalItem
        ApprovalItemDTO savedApprovalItemDTO = approvalItemService.save(approvalItemDTO);

        // 删除ApprovalItem
        approvalItemService.delete(savedApprovalItemDTO.getId());

        // 验证删除成功
        Optional<ApprovalItem> foundItem = approvalItemRepository.findById(savedApprovalItemDTO.getId());
        assertThat(foundItem).isNotPresent();
    }
}