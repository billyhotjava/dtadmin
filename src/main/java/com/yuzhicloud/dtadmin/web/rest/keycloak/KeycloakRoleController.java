package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
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
 * Keycloak角色管理REST控制器
 * 提供角色的CRUD操作API
 */
@RestController
@RequestMapping("/api/keycloak/roles")
@PreAuthorize("hasRole('ADMIN')")
public class KeycloakRoleController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleController.class);
    
    private final KeycloakRoleService roleService;

    public KeycloakRoleController(KeycloakRoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有Realm角色
     */
    @GetMapping
    public ResponseEntity<List<KeycloakRoleDTO>> getAllRealmRoles() {
        try {
            List<KeycloakRoleDTO> roles = roleService.getAllRealmRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error retrieving realm roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据名称获取Realm角色
     */
    @GetMapping("/{roleName}")
    public ResponseEntity<KeycloakRoleDTO> getRealmRoleByName(@PathVariable String roleName) {
        try {
            KeycloakRoleDTO role = roleService.getRealmRoleByName(roleName);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("Error retrieving realm role by name: {}", roleName, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 创建新的Realm角色
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createRealmRole(@RequestBody KeycloakRoleDTO role) {
        try {
            roleService.createRealmRole(role);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Realm role created successfully"));
        } catch (Exception e) {
            logger.error("Error creating realm role: {}", role.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create realm role: " + e.getMessage()));
        }
    }

    /**
     * 更新Realm角色
     */
    @PutMapping("/{roleName}")
    public ResponseEntity<Map<String, String>> updateRealmRole(
            @PathVariable String roleName, 
            @RequestBody KeycloakRoleDTO role) {
        try {
            roleService.updateRealmRole(roleName, role);
            return ResponseEntity.ok(Map.of("message", "Realm role updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating realm role: {}", roleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update realm role: " + e.getMessage()));
        }
    }

    /**
     * 删除Realm角色
     */
    @DeleteMapping("/{roleName}")
    public ResponseEntity<Map<String, String>> deleteRealmRole(@PathVariable String roleName) {
        try {
            roleService.deleteRealmRole(roleName);
            return ResponseEntity.ok(Map.of("message", "Realm role deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting realm role: {}", roleName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete realm role: " + e.getMessage()));
        }
    }
}