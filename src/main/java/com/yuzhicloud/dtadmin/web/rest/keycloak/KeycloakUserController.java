package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleAssignmentDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakRoleService;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.service.ApprovalRequestService;
import com.yuzhi.dtadmin.service.AuditLogUtil;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Keycloak用户管理REST控制器
 * 提供用户的CRUD操作API
 */
@RestController
@RequestMapping("/api/keycloak/users")
// @PreAuthorize("hasRole('ADMIN')") // 暂时禁用，由Keycloak处理权限
public class KeycloakUserController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserController.class);
    
    private final KeycloakUserService userService;
    private final KeycloakRoleService roleService;
    private final ApprovalRequestService approvalRequestService;
    private final ApprovalRequestMapper approvalRequestMapper;
    private final ObjectMapper objectMapper;
    private final AuditLogUtil auditLogUtil;

    public KeycloakUserController(KeycloakUserService userService, KeycloakRoleService roleService, 
                                  ApprovalRequestService approvalRequestService, ApprovalRequestMapper approvalRequestMapper,
                                  AuditLogUtil auditLogUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.approvalRequestService = approvalRequestService;
        this.approvalRequestMapper = approvalRequestMapper;
        this.objectMapper = new ObjectMapper();
        this.auditLogUtil = auditLogUtil;
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<KeycloakUserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int first,
            @RequestParam(defaultValue = "100") int max) {
        try {
            List<KeycloakUserDTO> users = userService.getAllUsers(first, max);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据用户名搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<List<KeycloakUserDTO>> searchUsers(
            @RequestParam("username") String username) {
        try {
            List<KeycloakUserDTO> users = userService.searchUsersByUsername(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error searching users by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{userId}")
    public ResponseEntity<KeycloakUserDTO> getUserById(@PathVariable String userId) {
        try {
            KeycloakUserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 创建新用户 - 改为创建审批请求
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody KeycloakUserDTO user) {
        try {
            // 创建审批请求而不是直接创建用户
            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setRequester("current_user"); // TODO: 获取当前用户
            approvalRequest.setApprover(null); // 审批人将在审批时设置
            approvalRequest.setDecisionNote(null); // 审批意见将在审批时设置
            approvalRequest.setErrorMessage(null); // 错误信息将在失败时设置
            approvalRequest.setType(ApprovalType.CREATE_USER);
            approvalRequest.setReason("创建新用户: " + user.getUsername());
            approvalRequest.setCreatedAt(Instant.now());
            approvalRequest.setStatus(ApprovalStatus.PENDING);
            
            // 创建审批项
            ApprovalItem approvalItem = new ApprovalItem();
            approvalItem.setTargetKind("USER");
            approvalItem.setTargetId(UUID.randomUUID().toString()); // 临时ID
            approvalItem.setSeqNumber(1);
            approvalItem.setPayload(convertUserToString(user));
            approvalItem.setRequest(approvalRequest);
            
            Set<ApprovalItem> items = new HashSet<>();
            items.add(approvalItem);
            approvalRequest.setItems(items);
            
            // 保存审批请求
            ApprovalRequestDTO savedRequest = approvalRequestService.save(approvalRequestMapper.toDto(approvalRequest));
            
            // 记录审计日志
            auditLogUtil.logApprovalRequestCreated("current_user", savedRequest.getId(), "CREATE_USER", "创建新用户: " + user.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("requestId", savedRequest.getId().toString(), "message", "User creation request submitted for approval"));
        } catch (Exception e) {
            logger.error("Error creating user request: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit user creation request: " + e.getMessage()));
        }
    }

    /**
     * 更新用户信息 - 改为创建审批请求
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable String userId, 
            @RequestBody KeycloakUserDTO user) {
                 logger.info("get updating user request: {}");
        try {
            // 创建审批请求而不是直接更新用户
            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setRequester("current_user"); // TODO: 获取当前用户
            approvalRequest.setApprover(null); // 审批人将在审批时设置
            approvalRequest.setDecisionNote(null); // 审批意见将在审批时设置
            approvalRequest.setErrorMessage(null); // 错误信息将在失败时设置
            approvalRequest.setType(ApprovalType.UPDATE_USER);
            approvalRequest.setReason("更新用户信息: " + userId);
            approvalRequest.setCreatedAt(Instant.now());
            approvalRequest.setStatus(ApprovalStatus.PENDING);
            
            // 创建审批项
            ApprovalItem approvalItem = new ApprovalItem();
            approvalItem.setTargetKind("USER");
            approvalItem.setTargetId(userId);
            approvalItem.setSeqNumber(1);
            approvalItem.setPayload(convertUserToString(user));
            approvalItem.setRequest(approvalRequest);
            
            Set<ApprovalItem> items = new HashSet<>();
            items.add(approvalItem);
            approvalRequest.setItems(items);
            
            // 保存审批请求
            ApprovalRequestDTO savedRequest = approvalRequestService.save(approvalRequestMapper.toDto(approvalRequest));
            
            // 记录审计日志
            auditLogUtil.logApprovalRequestCreated("current_user", savedRequest.getId(), "UPDATE_USER", "更新用户信息: " + userId);
            
            return ResponseEntity.ok(Map.of("requestId", savedRequest.getId().toString(), "message", "User update request submitted for approval"));
        } catch (Exception e) {
            logger.error("Error updating user request: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit user update request: " + e.getMessage()));
        }
    }

    /**
     * 删除用户 - 改为创建审批请求
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        try {
            // 获取用户信息用于保存到审批请求中
            KeycloakUserDTO user = userService.getUserById(userId);
            
            // 创建审批请求而不是直接删除用户
            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setRequester("current_user"); // TODO: 获取当前用户
            approvalRequest.setApprover(null); // 审批人将在审批时设置
            approvalRequest.setDecisionNote(null); // 审批意见将在审批时设置
            approvalRequest.setErrorMessage(null); // 错误信息将在失败时设置
            approvalRequest.setType(ApprovalType.DELETE_USER);
            approvalRequest.setReason("删除用户: " + userId + " (" + user.getUsername() + ")");
            approvalRequest.setCreatedAt(Instant.now());
            approvalRequest.setStatus(ApprovalStatus.PENDING);
            
            // 创建审批项
            ApprovalItem approvalItem = new ApprovalItem();
            approvalItem.setTargetKind("USER");
            approvalItem.setTargetId(userId);
            approvalItem.setSeqNumber(1);
            approvalItem.setPayload(convertUserToString(user)); // 保存用户信息用于审批查看
            approvalItem.setRequest(approvalRequest);
            
            Set<ApprovalItem> items = new HashSet<>();
            items.add(approvalItem);
            approvalRequest.setItems(items);
            
            // 保存审批请求
            ApprovalRequestDTO savedRequest = approvalRequestService.save(approvalRequestMapper.toDto(approvalRequest));
            
            // 记录审计日志
            auditLogUtil.logApprovalRequestCreated("current_user", savedRequest.getId(), "DELETE_USER", "删除用户: " + userId + " (" + user.getUsername() + ")");
            
            return ResponseEntity.ok(Map.of("requestId", savedRequest.getId().toString(), "message", "User deletion request submitted for approval"));
        } catch (Exception e) {
            logger.error("Error deleting user request: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit user deletion request: " + e.getMessage()));
        }
    }

    /**
     * 将用户对象转换为字符串
     */
    private String convertUserToString(KeycloakUserDTO user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            logger.error("Error converting user to string", e);
            return "";
        }
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String userId,
            @RequestBody Map<String, Object> passwordData) {
        try {
            String newPassword = (String) passwordData.get("password");
            Boolean temporary = (Boolean) passwordData.getOrDefault("temporary", false);
            
            userService.resetUserPassword(userId, newPassword, temporary);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset password: " + e.getMessage()));
        }
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{userId}/enabled")
    public ResponseEntity<Map<String, String>> setUserEnabled(
            @PathVariable String userId,
            @RequestBody Map<String, Boolean> enabledData) {
        try {
            Boolean enabled = enabledData.get("enabled");
            userService.setUserEnabled(userId, enabled);
            return ResponseEntity.ok(Map.of("message", 
                    enabled ? "User enabled successfully" : "User disabled successfully"));
        } catch (Exception e) {
            logger.error("Error setting user enabled status: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<KeycloakRoleDTO>> getUserRoles(@PathVariable String userId) {
        try {
            List<KeycloakRoleDTO> roles = roleService.getUserRealmRoles(userId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error retrieving user roles: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 为用户分配角色 - 改为创建审批请求
     */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<Map<String, String>> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<KeycloakRoleDTO> roles) {
        try {
            // 创建审批请求而不是直接分配角色
            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setRequester("current_user"); // TODO: 获取当前用户
            approvalRequest.setApprover(null); // 审批人将在审批时设置
            approvalRequest.setDecisionNote(null); // 审批意见将在审批时设置
            approvalRequest.setErrorMessage(null); // 错误信息将在失败时设置
            approvalRequest.setType(ApprovalType.GRANT_ROLE);
            approvalRequest.setReason("为用户分配角色: " + userId);
            approvalRequest.setCreatedAt(Instant.now());
            approvalRequest.setStatus(ApprovalStatus.PENDING);
            
            // 创建审批项
            Set<ApprovalItem> items = new HashSet<>();
            // 创建角色分配DTO并转换为JSON字符串
            KeycloakRoleAssignmentDTO roleAssignment = new KeycloakRoleAssignmentDTO(roles);
            String payload = convertRoleAssignmentToString(roleAssignment);
            
            for (int i = 0; i < roles.size(); i++) {
                ApprovalItem approvalItem = new ApprovalItem();
                approvalItem.setTargetKind("USER");
                approvalItem.setTargetId(userId); // 用户ID放在targetId字段中
                approvalItem.setSeqNumber(i + 1);
                approvalItem.setPayload(payload); // 角色信息作为payload
                approvalItem.setRequest(approvalRequest);
                items.add(approvalItem);
            }
            
            approvalRequest.setItems(items);
            
            // 保存审批请求
            ApprovalRequestDTO savedRequest = approvalRequestService.save(approvalRequestMapper.toDto(approvalRequest));
            
            // 记录审计日志
            auditLogUtil.logApprovalRequestCreated("current_user", savedRequest.getId(), "GRANT_ROLE", "为用户分配角色: " + userId);
            
            return ResponseEntity.ok(Map.of("requestId", savedRequest.getId().toString(), "message", "Role assignment request submitted for approval"));
        } catch (Exception e) {
            logger.error("Error assigning roles to user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit role assignment request: " + e.getMessage()));
        }
    }

    /**
     * 移除用户的角色 - 改为创建审批请求
     */
    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<Map<String, String>> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<KeycloakRoleDTO> roles) {
        try {
            // 创建审批请求而不是直接移除角色
            ApprovalRequest approvalRequest = new ApprovalRequest();
            approvalRequest.setRequester("current_user"); // TODO: 获取当前用户
            approvalRequest.setApprover(null); // 审批人将在审批时设置
            approvalRequest.setDecisionNote(null); // 审批意见将在审批时设置
            approvalRequest.setErrorMessage(null); // 错误信息将在失败时设置
            approvalRequest.setType(ApprovalType.REVOKE_ROLE);
            approvalRequest.setReason("为用户移除角色: " + userId);
            approvalRequest.setCreatedAt(Instant.now());
            approvalRequest.setStatus(ApprovalStatus.PENDING);
            
            // 创建审批项
            Set<ApprovalItem> items = new HashSet<>();
            // 创建角色分配DTO并转换为JSON字符串
            KeycloakRoleAssignmentDTO roleAssignment = new KeycloakRoleAssignmentDTO(roles);
            String payload = convertRoleAssignmentToString(roleAssignment);
            
            for (int i = 0; i < roles.size(); i++) {
                ApprovalItem approvalItem = new ApprovalItem();
                approvalItem.setTargetKind("USER");
                approvalItem.setTargetId(userId); // 用户ID放在targetId字段中
                approvalItem.setSeqNumber(i + 1);
                approvalItem.setPayload(payload); // 角色信息作为payload
                approvalItem.setRequest(approvalRequest);
                items.add(approvalItem);
            }
            
            approvalRequest.setItems(items);
            
            // 保存审批请求
            ApprovalRequestDTO savedRequest = approvalRequestService.save(approvalRequestMapper.toDto(approvalRequest));
            
            // 记录审计日志
            auditLogUtil.logApprovalRequestCreated("current_user", savedRequest.getId(), "REVOKE_ROLE", "为用户移除角色: " + userId);
            
            return ResponseEntity.ok(Map.of("requestId", savedRequest.getId().toString(), "message", "Role removal request submitted for approval"));
        } catch (Exception e) {
            logger.error("Error removing roles from user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit role removal request: " + e.getMessage()));
        }
    }
    
    /**
     * 将角色分配对象转换为字符串
     */
    private String convertRoleAssignmentToString(KeycloakRoleAssignmentDTO roleAssignment) {
        try {
            return objectMapper.writeValueAsString(roleAssignment);
        } catch (Exception e) {
            logger.error("Error converting role assignment to string", e);
            return "";
        }
    }
}