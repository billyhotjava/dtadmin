package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakGroupDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Keycloak组管理REST控制器
 * 提供组的CRUD操作API
 */
@RestController
@RequestMapping("/api/keycloak/groups")
// @PreAuthorize("hasRole('ADMIN')") // 暂时禁用，由Keycloak处理权限
public class KeycloakGroupController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakGroupController.class);
    
    private final KeycloakGroupService groupService;

    public KeycloakGroupController(KeycloakGroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 获取所有组
     */
    @GetMapping
    public ResponseEntity<List<KeycloakGroupDTO>> getAllGroups() {
        try {
            List<KeycloakGroupDTO> groups = groupService.getAllGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving groups", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取组
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<KeycloakGroupDTO> getGroupById(@PathVariable String groupId) {
        try {
            KeycloakGroupDTO group = groupService.getGroupById(groupId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            logger.error("Error retrieving group by ID: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 创建新组
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createGroup(@RequestBody KeycloakGroupDTO group) {
        try {
            String groupId = groupService.createGroup(group);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("groupId", groupId != null ? groupId : "", "message", "Group created successfully"));
        } catch (Exception e) {
            logger.error("Error creating group: {}", group.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create group: " + e.getMessage()));
        }
    }

    /**
     * 更新组信息
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<Map<String, String>> updateGroup(
            @PathVariable String groupId, 
            @RequestBody KeycloakGroupDTO group) {
        try {
            groupService.updateGroup(groupId, group);
            return ResponseEntity.ok(Map.of("message", "Group updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating group: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update group: " + e.getMessage()));
        }
    }

    /**
     * 删除组
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable String groupId) {
        try {
            groupService.deleteGroup(groupId);
            return ResponseEntity.ok(Map.of("message", "Group deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting group: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete group: " + e.getMessage()));
        }
    }

    /**
     * 获取组成员
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupId) {
        try {
            List<String> members = groupService.getGroupMembers(groupId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            logger.error("Error retrieving group members: {}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 将用户加入组
     */
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Map<String, String>> addUserToGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        try {
            groupService.addUserToGroup(groupId, userId);
            return ResponseEntity.ok(Map.of("message", "User added to group successfully"));
        } catch (Exception e) {
            logger.error("Error adding user {} to group {}", userId, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add user to group: " + e.getMessage()));
        }
    }

    /**
     * 将用户从组中移除
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeUserFromGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        try {
            groupService.removeUserFromGroup(groupId, userId);
            return ResponseEntity.ok(Map.of("message", "User removed from group successfully"));
        } catch (Exception e) {
            logger.error("Error removing user {} from group {}", userId, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove user from group: " + e.getMessage()));
        }
    }

    /**
     * 获取用户所属的组
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<KeycloakGroupDTO>> getUserGroups(@PathVariable String userId) {
        try {
            List<KeycloakGroupDTO> groups = groupService.getUserGroups(userId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            logger.error("Error retrieving user groups for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}