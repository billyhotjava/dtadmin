package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.service.keycloak.KeycloakApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Keycloak审批管理REST控制器
 * 管理用户相关操作的审批流程
 */
@RestController
@RequestMapping("/api/keycloak/approvals")
public class KeycloakApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakApprovalController.class);
    
    private final KeycloakApprovalService approvalService;

    public KeycloakApprovalController(KeycloakApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * 审批通过请求
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Map<String, String>> approveRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> data) {
        try {
            String approver = data.getOrDefault("approver", "unknown");
            String note = data.getOrDefault("note", "");
            
            approvalService.approveRequest(requestId, approver, note);
            return ResponseEntity.ok(Map.of("message", "Request approved successfully"));
        } catch (Exception e) {
            logger.error("Error approving request: {}", requestId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to approve request: " + e.getMessage()));
        }
    }

    /**
     * 审批拒绝请求
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Map<String, String>> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> data) {
        try {
            String approver = data.getOrDefault("approver", "unknown");
            String note = data.getOrDefault("note", "");
            
            approvalService.rejectRequest(requestId, approver, note);
            return ResponseEntity.ok(Map.of("message", "Request rejected successfully"));
        } catch (Exception e) {
            logger.error("Error rejecting request: {}", requestId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to reject request: " + e.getMessage()));
        }
    }
    
    /**
     * 处理已批准的请求并同步到Keycloak
     */
    @PostMapping("/{requestId}/process")
    public ResponseEntity<Map<String, String>> processApprovedRequest(@PathVariable Long requestId) {
        try {
            approvalService.processApprovedRequest(requestId);
            return ResponseEntity.ok(Map.of("message", "Approved request processed successfully"));
        } catch (Exception e) {
            logger.error("Error processing approved request: {}", requestId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to process approved request: " + e.getMessage()));
        }
    }
}