package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak诊断控制器 - 用于检查配置问题
 * 无需认证即可访问，用于排查认证问题
 */
@RestController
@RequestMapping("/api/keycloak/diagnostic")
public class KeycloakDiagnosticController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakDiagnosticController.class);

    private final KeycloakConfig keycloakConfig;
    private final KeycloakTestService testService;

    public KeycloakDiagnosticController(KeycloakConfig keycloakConfig, 
                                        KeycloakTestService testService) {
        this.keycloakConfig = keycloakConfig;
        this.testService = testService;
    }

    /**
     * 检查Keycloak配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<?> checkConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("keycloakServerUrl", keycloakConfig.getKeycloakServerUrl());
            config.put("authRealm", keycloakConfig.getRealm());
            config.put("targetRealm", keycloakConfig.getTargetRealm());
            config.put("adminUsername", keycloakConfig.getAdminUsername());
            config.put("adminClientId", keycloakConfig.getAdminClientId());
            config.put("clientId", keycloakConfig.getClientId());
            config.put("hasClientSecret", keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().trim().isEmpty());
            
            logger.info("Configuration check completed");
            return ResponseEntity.ok(createSuccessResponse(config));
            
        } catch (Exception e) {
            logger.error("Configuration check failed", e);
            return ResponseEntity.ok(createErrorResponse("配置检查失败：" + e.getMessage()));
        }
    }

    /**
     * 测试管理员登录
     */
    @PostMapping("/test-admin-login")
    public ResponseEntity<?> testAdminLogin() {
        try {
            logger.info("Testing admin login...");
            testService.testAdminLogin();
            
            logger.info("Admin login test successful");
            return ResponseEntity.ok(createSuccessResponse("管理员登录测试成功"));
            
        } catch (Exception e) {
            logger.error("Admin login test failed", e);
            return ResponseEntity.ok(createErrorResponse("管理员登录测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试用户登录（使用配置的客户端）
     */
    @PostMapping("/test-user-login")
    public ResponseEntity<?> testUserLogin(@RequestBody TestLoginRequest request) {
        try {
            logger.info("Testing user login with configured client...");
            testService.testUserLogin(request.getUsername(), request.getPassword());
            
            logger.info("User login test successful");
            return ResponseEntity.ok(createSuccessResponse("用户登录测试成功（使用配置的客户端）"));
            
        } catch (Exception e) {
            logger.error("User login test failed", e);
            return ResponseEntity.ok(createErrorResponse("用户登录测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试用户登录（使用admin-cli客户端）
     */
    @PostMapping("/test-user-login-admin-cli")
    public ResponseEntity<?> testUserLoginWithAdminCli(@RequestBody TestLoginRequest request) {
        try {
            logger.info("Testing user login with admin-cli client...");
            testService.testUserLoginWithAdminCli(request.getUsername(), request.getPassword());
            
            logger.info("User login with admin-cli test successful");
            return ResponseEntity.ok(createSuccessResponse("用户登录测试成功（使用admin-cli客户端）"));
            
        } catch (Exception e) {
            logger.error("User login with admin-cli test failed", e);
            return ResponseEntity.ok(createErrorResponse("使用admin-cli客户端登录测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试登录请求DTO
     */
    public static class TestLoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}