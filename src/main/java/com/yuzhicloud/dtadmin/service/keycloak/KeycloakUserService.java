package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keycloak用户管理服务
 * 提供用户的CRUD操作
 */
@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;
    private final KeycloakAuthService authService;

    public KeycloakUserService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                              KeycloakConfig keycloakConfig,
                              KeycloakAuthService authService) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
        this.authService = authService;
    }

    /**
     * 获取所有用户
     */
    public List<KeycloakUserDTO> getAllUsers() {
        return getAllUsers(0, 100);
    }

    /**
     * 分页获取用户
     */
    public List<KeycloakUserDTO> getAllUsers(int first, int max) {
        try {
            String url = String.format("%s/admin/realms/%s/users?first=%d&max=%d",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    first, max);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakUserDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakUserDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Successfully retrieved {} users", 
                        response.getBody() != null ? response.getBody().size() : 0);
                return response.getBody();
            } else {
                logger.error("Failed to retrieve users, status: {}", response.getStatusCode());

                throw new RuntimeException("Failed to retrieve users from Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error retrieving users from Keycloak", e);
            throw new RuntimeException("Error retrieving users from Keycloak", e);
        }
    }

    /**
     * 根据用户名搜索用户
     */
    public List<KeycloakUserDTO> searchUsersByUsername(String username) {
        try {
            String url = String.format("%s/admin/realms/%s/users?username=%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    username);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakUserDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakUserDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to search users by username, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to search users by username");
            }
        } catch (Exception e) {
            logger.error("Error searching users by username", e);
            throw new RuntimeException("Error searching users by username", e);
        }
    }

    /**
     * 根据ID获取用户
     */
    public KeycloakUserDTO getUserById(String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<KeycloakUserDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, KeycloakUserDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get user by ID, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get user by ID");
            }
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", userId, e);
            throw new RuntimeException("Error getting user by ID: " + userId, e);
        }
    }

    /**
     * 创建新用户
     */
    public String createUser(KeycloakUserDTO user) {
        try {
            String url = String.format("%s/admin/realms/%s/users",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpEntity<KeycloakUserDTO> entity = authService.createAuthEntity(user);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                // 从Location头获取新创建用户的ID
                String location = response.getHeaders().getFirst("Location");
                if (location != null) {
                    String userId = location.substring(location.lastIndexOf('/') + 1);
                    logger.info("Successfully created user: {}", user.getUsername());
                    return userId;
                }
                throw new RuntimeException("User created but could not retrieve user ID");
            } else {
                logger.error("Failed to create user, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create user");
            }
        } catch (Exception e) {
            logger.error("Error creating user: {}", user.getUsername(), e);
            throw new RuntimeException("Error creating user: " + user.getUsername(), e);
        }
    }

    /**
     * 更新用户信息
     */
    public void updateUser(String userId, KeycloakUserDTO user) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<KeycloakUserDTO> entity = authService.createAuthEntity(user);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully updated user: {}", userId);
            } else {
                logger.error("Failed to update user, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to update user");
            }
        } catch (Exception e) {
            logger.error("Error updating user: {}", userId, e);
            throw new RuntimeException("Error updating user: " + userId, e);
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully deleted user: {}", userId);
            } else {
                logger.error("Failed to delete user, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to delete user");
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            throw new RuntimeException("Error deleting user: " + userId, e);
        }
    }

    /**
     * 重置用户密码
     */
    public void resetUserPassword(String userId, String newPassword, boolean temporary) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/reset-password",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            Map<String, Object> passwordData = new HashMap<>();
            passwordData.put("type", "password");
            passwordData.put("value", newPassword);
            passwordData.put("temporary", temporary);

            HttpEntity<Map<String, Object>> entity = authService.createAuthEntity(passwordData);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully reset password for user: {}", userId);
            } else {
                logger.error("Failed to reset password, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to reset password");
            }
        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", userId, e);
            throw new RuntimeException("Error resetting password for user: " + userId, e);
        }
    }

    /**
     * 启用/禁用用户
     */
    public void setUserEnabled(String userId, boolean enabled) {
        try {
            KeycloakUserDTO user = getUserById(userId);
            user.setEnabled(enabled);
            updateUser(userId, user);
            logger.info("Successfully {} user: {}", enabled ? "enabled" : "disabled", userId);
        } catch (Exception e) {
            logger.error("Error {} user: {}", enabled ? "enabling" : "disabling", userId, e);
            throw new RuntimeException("Error " + (enabled ? "enabling" : "disabling") + " user: " + userId, e);
        }
    }
}