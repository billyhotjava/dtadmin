package com.yuzhicloud.dtadmin.service.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Keycloak用户同步服务
 * 处理审批通过后的用户操作同步到Keycloak
 */
@Service
public class KeycloakUserSyncService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserSyncService.class);
    
    private final KeycloakUserService keycloakUserService;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ObjectMapper objectMapper;

    public KeycloakUserSyncService(KeycloakUserService keycloakUserService, 
                                  ApprovalRequestRepository approvalRequestRepository) {
        this.keycloakUserService = keycloakUserService;
        this.approvalRequestRepository = approvalRequestRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 处理审批通过的请求
     */
    @Transactional
    public void processApprovedRequest(Long requestId) {
        try {
            ApprovalRequest request = approvalRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Approval request not found: " + requestId));
            
            // 检查状态是否为APPROVED
            if (request.getStatus() != ApprovalStatus.APPROVED) {
                logger.warn("Request {} is not approved, current status: {}", requestId, request.getStatus());
                return;
            }
            
            // 根据请求类型处理
            switch (request.getType()) {
                case CREATE_USER:
                    handleCreateUser(request);
                    break;
                case UPDATE_USER:
                    handleUpdateUser(request);
                    break;
                case DELETE_USER:
                    handleDeleteUser(request);
                    break;
                case GRANT_ROLE:
                    handleGrantRole(request);
                    break;
                case REVOKE_ROLE:
                    handleRevokeRole(request);
                    break;
                default:
                    logger.warn("Unsupported approval type: {}", request.getType());
                    return;
            }
            
            // 更新状态为APPLIED
            request.setStatus(ApprovalStatus.APPLIED);
            approvalRequestRepository.save(request);
            
            logger.info("Successfully processed approval request: {}", requestId);
        } catch (Exception e) {
            logger.error("Error processing approval request: {}", requestId, e);
            // 更新状态为FAILED
            try {
                ApprovalRequest request = approvalRequestRepository.findById(requestId).orElse(null);
                if (request != null) {
                    request.setStatus(ApprovalStatus.FAILED);
                    request.setErrorMessage(e.getMessage());
                    approvalRequestRepository.save(request);
                }
            } catch (Exception ex) {
                logger.error("Error updating request status to FAILED: {}", requestId, ex);
            }
            throw new RuntimeException("Failed to process approval request", e);
        }
    }

    /**
     * 处理创建用户请求
     */
    private void handleCreateUser(ApprovalRequest request) {
        Set<ApprovalItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No approval items found for CREATE_USER request");
        }
        
        // 只处理第一个item
        ApprovalItem item = items.iterator().next();
        try {
            KeycloakUserDTO user = objectMapper.readValue(item.getPayload(), KeycloakUserDTO.class);
            String userId = keycloakUserService.createUser(user);
            logger.info("Created user {} with Keycloak ID: {}", user.getUsername(), userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }

    /**
     * 处理更新用户请求
     */
    private void handleUpdateUser(ApprovalRequest request) {
        Set<ApprovalItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No approval items found for UPDATE_USER request");
        }
        
        // 只处理第一个item
        ApprovalItem item = items.iterator().next();
        try {
            KeycloakUserDTO user = objectMapper.readValue(item.getPayload(), KeycloakUserDTO.class);
            keycloakUserService.updateUser(item.getTargetId(), user);
            logger.info("Updated user with Keycloak ID: {}", item.getTargetId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }

    /**
     * 处理删除用户请求
     */
    private void handleDeleteUser(ApprovalRequest request) {
        Set<ApprovalItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No approval items found for DELETE_USER request");
        }
        
        // 只处理第一个item
        ApprovalItem item = items.iterator().next();
        try {
            keycloakUserService.deleteUser(item.getTargetId());
            logger.info("Deleted user with Keycloak ID: {}", item.getTargetId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user in Keycloak", e);
        }
    }

    /**
     * 处理分配角色请求
     */
    private void handleGrantRole(ApprovalRequest request) {
        // TODO: 实现角色分配逻辑
        logger.info("Grant role request processed: {}", request.getId());
    }

    /**
     * 处理移除角色请求
     */
    private void handleRevokeRole(ApprovalRequest request) {
        // TODO: 实现角色移除逻辑
        logger.info("Revoke role request processed: {}", request.getId());
    }
}