package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Keycloak用户同步REST控制器
 * 处理审批通过后的用户操作同步到Keycloak
 */
@RestController
@RequestMapping("/api/keycloak/user-sync")
public class KeycloakUserSyncController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserSyncController.class);
    
    private final KeycloakUserSyncService userSyncService;

    public KeycloakUserSyncController(KeycloakUserSyncService userSyncService) {
        this.userSyncService = userSyncService;
    }

    /**
     * 处理审批通过的请求
     */
    @PostMapping("/process/{requestId}")
    public ResponseEntity<Map<String, String>> processApprovedRequest(@PathVariable Long requestId) {
        try {
            userSyncService.processApprovedRequest(requestId);
            return ResponseEntity.ok(Map.of("message", "Approval request processed successfully"));
        } catch (Exception e) {
            logger.error("Error processing approval request: {}", requestId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to process approval request: " + e.getMessage()));
        }
    }
}