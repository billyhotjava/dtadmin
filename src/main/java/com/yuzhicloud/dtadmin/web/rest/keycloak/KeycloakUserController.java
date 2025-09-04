package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    public KeycloakUserController(KeycloakUserService userService, KeycloakRoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody KeycloakUserDTO user) {
        try {
            String userId = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("userId", userId, "message", "User created successfully"));
        } catch (Exception e) {
            logger.error("Error creating user: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable String userId, 
            @RequestBody KeycloakUserDTO user) {
        try {
            userService.updateUser(userId, user);
            return ResponseEntity.ok(Map.of("message", "User updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
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
     * 为用户分配角色
     */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<Map<String, String>> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<KeycloakRoleDTO> roles) {
        try {
            roleService.assignRealmRolesToUser(userId, roles);
            return ResponseEntity.ok(Map.of("message", "Roles assigned successfully"));
        } catch (Exception e) {
            logger.error("Error assigning roles to user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to assign roles: " + e.getMessage()));
        }
    }

    /**
     * 移除用户的角色
     */
    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<Map<String, String>> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<KeycloakRoleDTO> roles) {
        try {
            roleService.removeRealmRolesFromUser(userId, roles);
            return ResponseEntity.ok(Map.of("message", "Roles removed successfully"));
        } catch (Exception e) {
            logger.error("Error removing roles from user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove roles: " + e.getMessage()));
        }
    }
}