package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakTokenDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak配置诊断控制器
 * 用于诊断Keycloak配置问题
 */
@RestController
@RequestMapping("/api/keycloak/diagnostics")
public class KeycloakDiagnosticsController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakDiagnosticsController.class);
    
    private final KeycloakConfig keycloakConfig;
    private final KeycloakTestService testService;

    public KeycloakDiagnosticsController(KeycloakConfig keycloakConfig, KeycloakTestService testService) {
        this.keycloakConfig = keycloakConfig;
        this.testService = testService;
    }

    /**
     * 获取Keycloak配置信息（隐藏敏感信息）
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("serverUrl", keycloakConfig.getKeycloakServerUrl());
            config.put("authRealm", keycloakConfig.getRealm());
            config.put("targetRealm", keycloakConfig.getTargetRealm());
            config.put("clientId", keycloakConfig.getClientId());
            config.put("adminClientId", keycloakConfig.getAdminClientId());
            config.put("adminUsername", keycloakConfig.getAdminUsername());
            config.put("hasClientSecret", keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().trim().isEmpty());
            config.put("hasAdminPassword", keycloakConfig.getAdminPassword() != null && !keycloakConfig.getAdminPassword().trim().isEmpty());
            config.put("trustAllCertificates", keycloakConfig.isTrustAllCertificates());
            
            // 构建完整的URLs
            config.put("tokenUrl", String.format("%s/realms/%s/protocol/openid-connect/token", 
                    keycloakConfig.getKeycloakServerUrl(), keycloakConfig.getTargetRealm()));
            config.put("adminTokenUrl", String.format("%s/realms/%s/protocol/openid-connect/token", 
                    keycloakConfig.getKeycloakServerUrl(), keycloakConfig.getRealm()));
            config.put("adminApiUrl", String.format("%s/admin/realms/%s", 
                    keycloakConfig.getKeycloakServerUrl(), keycloakConfig.getTargetRealm()));
            
            return ResponseEntity.ok(createSuccessResponse(config));
        } catch (Exception e) {
            logger.error("Error getting config", e);
            return ResponseEntity.status(500).body(createErrorResponse("获取配置失败：" + e.getMessage()));
        }
    }

    /**
     * 测试连接到Keycloak服务器
     */
    @PostMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            // 这里可以添加简单的连接测试逻辑
            // 例如ping Keycloak服务器或者获取realm信息
            
            Map<String, Object> result = new HashMap<>();
            result.put("serverUrl", keycloakConfig.getKeycloakServerUrl());
            result.put("status", "配置已加载，需要手动测试连接");
            result.put("suggestion", "请检查Keycloak服务器是否可访问，以及realm和client配置是否正确");
            
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            logger.error("Connection test failed", e);
            return ResponseEntity.status(500).body(createErrorResponse("连接测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试管理员登录
     */
    @PostMapping("/test-admin-login")
    public ResponseEntity<Map<String, Object>> testAdminLogin() {
        try {
            KeycloakTokenDTO token = testService.testAdminLogin();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "管理员登录成功");
            result.put("hasToken", token.getAccessToken() != null);
            result.put("tokenType", token.getTokenType());
            result.put("expiresIn", token.getExpiresIn());
            
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            logger.error("Admin login test failed", e);
            return ResponseEntity.status(500).body(createErrorResponse("管理员登录测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试用户登录
     */
    @PostMapping("/test-user-login")
    public ResponseEntity<Map<String, Object>> testUserLogin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名和密码不能为空"));
            }
            
            KeycloakTokenDTO token = testService.testUserLogin(username, password);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "用户登录成功");
            result.put("hasToken", token.getAccessToken() != null);
            result.put("tokenType", token.getTokenType());
            result.put("expiresIn", token.getExpiresIn());
            
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            logger.error("User login test failed", e);
            return ResponseEntity.status(500).body(createErrorResponse("用户登录测试失败：" + e.getMessage()));
        }
    }

    /**
     * 测试使用admin-cli客户端进行用户登录
     */
    @PostMapping("/test-user-login-admin-cli")
    public ResponseEntity<Map<String, Object>> testUserLoginWithAdminCli(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名和密码不能为空"));
            }
            
            KeycloakTokenDTO token = testService.testUserLoginWithAdminCli(username, password);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "使用admin-cli客户端用户登录成功");
            result.put("hasToken", token.getAccessToken() != null);
            result.put("tokenType", token.getTokenType());
            result.put("expiresIn", token.getExpiresIn());
            
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            logger.error("User login with admin-cli test failed", e);
            return ResponseEntity.status(500).body(createErrorResponse("使用admin-cli客户端用户登录测试失败：" + e.getMessage()));
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