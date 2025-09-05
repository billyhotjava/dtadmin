package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileConfigDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Keycloak UserProfile管理REST控制器
 * 提供UserProfile配置的API接口
 */
@RestController
@RequestMapping("/api/keycloak/userprofile")
// @PreAuthorize("hasRole('ADMIN')") // 暂时禁用，由Keycloak处理权限
public class KeycloakUserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserProfileController.class);
    
    private final KeycloakUserProfileService userProfileService;

    public KeycloakUserProfileController(KeycloakUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * 获取UserProfile配置
     */
    @GetMapping("/config")
    public ResponseEntity<UserProfileConfigDTO> getUserProfileConfig() {
        try {
            UserProfileConfigDTO config = userProfileService.getUserProfileConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error retrieving UserProfile config", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新UserProfile配置
     */
    @PutMapping("/config")
    public ResponseEntity<Map<String, String>> updateUserProfileConfig(
            @RequestBody UserProfileConfigDTO config) {
        try {
            userProfileService.updateUserProfileConfig(config);
            return ResponseEntity.ok(Map.of("message", "UserProfile config updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating UserProfile config", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update UserProfile config: " + e.getMessage()));
        }
    }

    /**
     * 检查UserProfile是否已配置
     */
    @GetMapping("/configured")
    public ResponseEntity<Map<String, Boolean>> isUserProfileConfigured() {
        try {
            boolean configured = userProfileService.isUserProfileConfigured();
            return ResponseEntity.ok(Map.of("configured", configured));
        } catch (Exception e) {
            logger.error("Error checking UserProfile configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取UserProfile中定义的所有属性名称
     */
    @GetMapping("/attributes")
    public ResponseEntity<List<String>> getUserProfileAttributeNames() {
        try {
            List<String> attributeNames = userProfileService.getUserProfileAttributeNames();
            return ResponseEntity.ok(attributeNames);
        } catch (Exception e) {
            logger.error("Error retrieving UserProfile attribute names", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 测试UserProfile服务连接
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testUserProfileService() {
        try {
            Map<String, Object> result = new java.util.HashMap<>();
            
            // 测试基本连接
            boolean configured = userProfileService.isUserProfileConfigured();
            result.put("configured", configured);
            result.put("message", "UserProfile service is accessible");
            
            if (configured) {
                // 如果配置存在，获取属性数量
                List<String> attributes = userProfileService.getUserProfileAttributeNames();
                result.put("attributeCount", attributes.size());
                result.put("attributes", attributes);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error testing UserProfile service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UserProfile service test failed: " + e.getMessage()));
        }
    }
}