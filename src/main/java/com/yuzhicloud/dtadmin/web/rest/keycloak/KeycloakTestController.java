package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.service.keycloak.KeycloakAuthService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakRoleService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak测试控制器
 * 用于测试Keycloak集成是否正常工作
 */
@RestController
@RequestMapping("/api/keycloak/test")
@PreAuthorize("hasRole('ADMIN')")
public class KeycloakTestController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakTestController.class);
    
    private final KeycloakAuthService authService;
    private final KeycloakUserService userService;
    private final KeycloakRoleService roleService;
    private final KeycloakGroupService groupService;

    public KeycloakTestController(KeycloakAuthService authService,
                                 KeycloakUserService userService,
                                 KeycloakRoleService roleService,
                                 KeycloakGroupService groupService) {
        this.authService = authService;
        this.userService = userService;
        this.roleService = roleService;
        this.groupService = groupService;
    }

    /**
     * 测试Keycloak连接
     */
    @GetMapping("/connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试获取访问令牌
            String token = authService.getAccessToken();
            result.put("status", "success");
            result.put("message", "Successfully connected to Keycloak");
            result.put("tokenReceived", token != null && !token.isEmpty());
            logger.info("Keycloak connection test successful");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to connect to Keycloak: " + e.getMessage());
            result.put("tokenReceived", false);
            logger.error("Keycloak connection test failed", e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 测试用户服务
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> testUserService() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var users = userService.getAllUsers(0, 5); // 获取前5个用户
            result.put("status", "success");
            result.put("message", "Successfully retrieved users from Keycloak");
            result.put("userCount", users.size());
            result.put("users", users);
            logger.info("User service test successful, retrieved {} users", users.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to retrieve users: " + e.getMessage());
            result.put("userCount", 0);
            logger.error("User service test failed", e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 测试角色服务
     */
    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> testRoleService() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var roles = roleService.getAllRealmRoles();
            result.put("status", "success");
            result.put("message", "Successfully retrieved roles from Keycloak");
            result.put("roleCount", roles.size());
            result.put("roles", roles);
            logger.info("Role service test successful, retrieved {} roles", roles.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to retrieve roles: " + e.getMessage());
            result.put("roleCount", 0);
            logger.error("Role service test failed", e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 测试组服务
     */
    @GetMapping("/groups")
    public ResponseEntity<Map<String, Object>> testGroupService() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var groups = groupService.getAllGroups();
            result.put("status", "success");
            result.put("message", "Successfully retrieved groups from Keycloak");
            result.put("groupCount", groups.size());
            result.put("groups", groups);
            logger.info("Group service test successful, retrieved {} groups", groups.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to retrieve groups: " + e.getMessage());
            result.put("groupCount", 0);
            logger.error("Group service test failed", e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 综合测试所有服务
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> testAllServices() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tests = new HashMap<>();
        
        // 测试连接
        try {
            authService.getAccessToken();
            tests.put("connection", "PASS");
        } catch (Exception e) {
            tests.put("connection", "FAIL: " + e.getMessage());
        }
        
        // 测试用户服务
        try {
            userService.getAllUsers(0, 1);
            tests.put("userService", "PASS");
        } catch (Exception e) {
            tests.put("userService", "FAIL: " + e.getMessage());
        }
        
        // 测试角色服务
        try {
            roleService.getAllRealmRoles();
            tests.put("roleService", "PASS");
        } catch (Exception e) {
            tests.put("roleService", "FAIL: " + e.getMessage());
        }
        
        // 测试组服务
        try {
            groupService.getAllGroups();
            tests.put("groupService", "PASS");
        } catch (Exception e) {
            tests.put("groupService", "FAIL: " + e.getMessage());
        }
        
        // 统计结果
        long passCount = tests.values().stream().mapToLong(v -> v.toString().startsWith("PASS") ? 1 : 0).sum();
        long totalCount = tests.size();
        
        result.put("summary", String.format("%d/%d tests passed", passCount, totalCount));
        result.put("allPassed", passCount == totalCount);
        result.put("tests", tests);
        
        logger.info("Comprehensive test completed: {}/{} tests passed", passCount, totalCount);
        
        return ResponseEntity.ok(result);
    }
}