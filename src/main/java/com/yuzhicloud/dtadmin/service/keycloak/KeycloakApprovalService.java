package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Keycloak审批服务
 * 管理用户相关操作的审批流程
 */
@Service
public class KeycloakApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakApprovalService.class);
    
    private final ApprovalRequestRepository approvalRequestRepository;
    private final KeycloakUserSyncService userSyncService;

    public KeycloakApprovalService(ApprovalRequestRepository approvalRequestRepository,
                                  KeycloakUserSyncService userSyncService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.userSyncService = userSyncService;
    }

    /**
     * 审批通过请求
     */
    @Transactional
    public void approveRequest(Long requestId, String approver, String note) {
        try {
            ApprovalRequest request = approvalRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Approval request not found: " + requestId));
            
            // 更新审批信息
            request.setStatus(ApprovalStatus.APPROVED);
            request.setApprover(approver);
            request.setDecisionNote(note);
            request.setDecidedAt(Instant.now());
            
            approvalRequestRepository.save(request);
            
            logger.info("Approved request: {}", requestId);
        } catch (Exception e) {
            logger.error("Error approving request: {}", requestId, e);
            throw new RuntimeException("Failed to approve request", e);
        }
    }

    /**
     * 审批拒绝请求
     */
    @Transactional
    public void rejectRequest(Long requestId, String approver, String note) {
        try {
            ApprovalRequest request = approvalRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Approval request not found: " + requestId));
            
            // 更新审批信息
            request.setStatus(ApprovalStatus.REJECTED);
            request.setApprover(approver);
            request.setDecisionNote(note);
            request.setDecidedAt(Instant.now());
            
            approvalRequestRepository.save(request);
            
            logger.info("Rejected request: {}", requestId);
        } catch (Exception e) {
            logger.error("Error rejecting request: {}", requestId, e);
            throw new RuntimeException("Failed to reject request", e);
        }
    }
    
    /**
     * 处理已批准的请求并同步到Keycloak
     */
    @Transactional
    public void processApprovedRequest(Long requestId) {
        userSyncService.processApprovedRequest(requestId);
    }
}