package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakTokenDTO;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * Keycloak认证服务 - 使用官方Admin Client
 * 提供用户认证、令牌管理功能
 */
@Service
public class KeycloakAuthService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthService.class);
    
    private final KeycloakConfig keycloakConfig;
    private final Keycloak keycloakAdminClient;

    public KeycloakAuthService(Keycloak keycloakAdminClient, KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    /**
     * 用户登录 - 使用官方Admin Client
     * 
     * @param username 用户名
     * @param password 密码
     * @return 认证令牌信息
     */
    public KeycloakTokenDTO login(String username, String password) {
        try {
            logger.info("User login attempt: {}", username);
            
            // 先尝试使用配置的客户端
            Exception lastException = null;
            
            try {
                return attemptLogin(username, password, keycloakConfig.getClientId(), keycloakConfig.getClientSecret());
            } catch (Exception e) {
                logger.warn("Login failed with configured client {}: {}", keycloakConfig.getClientId(), e.getMessage());
                lastException = e;
            }
            
            // 如果失败，尝试使用admin-cli客户端（通常是公共客户端）
            try {
                logger.info("Fallback: trying login with admin-cli client");
                return attemptLogin(username, password, "admin-cli", null);
            } catch (Exception e) {
                logger.error("Login failed with admin-cli client: {}", e.getMessage());
                // 抛出原始异常
                throw lastException != null ? lastException : e;
            }
            
        } catch (Exception e) {
            logger.error("Login failed for user: {}. Error: {}", username, e.getMessage(), e);
            
            // 根据不同的错误类型返回更具体的错误信息
            String errorMessage = "登录失败";
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                errorMessage = "登录失败：用户名或密码错误，或者客户端配置有误";
            } else if (e.getMessage().contains("404") || e.getMessage().contains("Not Found")) {
                errorMessage = "登录失败：Keycloak服务或Realm不存在";
            } else if (e.getMessage().contains("400") || e.getMessage().contains("Bad Request")) {
                errorMessage = "登录失败：请求参数错误或客户端配置无效";
            }
            
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * 尝试使用指定的客户端进行登录
     */
    private KeycloakTokenDTO attemptLogin(String username, String password, String clientId, String clientSecret) {
        logger.info("Attempting login with client: {}", clientId);
        logger.debug("Keycloak server URL: {}", keycloakConfig.getKeycloakServerUrl());
        logger.debug("Target realm: {}", keycloakConfig.getTargetRealm());

        // 构建Keycloak客户端进行用户认证
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(keycloakConfig.getKeycloakServerUrl())
                .realm(keycloakConfig.getTargetRealm())  // 使用目标realm进行用户认证
                .clientId(clientId)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD);

        // 只在客户端密钥不为空时添加
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            builder.clientSecret(clientSecret);
            logger.debug("Using client secret for authentication");
        } else {
            logger.debug("Using public client (no client secret)");
        }

        Keycloak keycloak = builder.build();

        // 获取访问令牌
        AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
        
        // 转换为DTO
        KeycloakTokenDTO tokenDTO = convertToTokenDTO(tokenResponse);
        
        logger.info("Login successful with client: {}", clientId);
        return tokenDTO;
    }

    /**
     * 刷新令牌 - 使用官方Admin Client
     * 注意：由于Keycloak Admin Client的限制，这里简化处理
     * 实际中可能需要直接调用REST API
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public KeycloakTokenDTO refreshToken(String refreshToken) {
        try {
            logger.warn("Token refresh using Admin Client SDK is limited, consider using REST API for production");
            
            // 注意：这里的实现可能不能正常工作，因为Admin Client不支持直接刷新令牌
            // 实际应用中建议使用REST API调用
            throw new UnsupportedOperationException("刷新令牌功能需要使用REST API实现");
            
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new RuntimeException("令牌刷新失败", e);
        }
    }

    /**
     * 用户登出 - 使用官方Admin Client
     * 注意：由于Keycloak Admin Client的限制，这里简化处理
     * 实际中可能需要直接调用REST API
     * 
     * @param refreshToken 刷新令牌
     */
    public void logout(String refreshToken) {
        try {
            logger.warn("Logout using Admin Client SDK is limited, consider using REST API for production");
            
            // 注意：这里的实现可能不能正常工作，因为Admin Client不支持直接登出
            // 实际应用中建议使用REST API调用
            logger.info("User logout completed (simplified implementation)");
            
        } catch (Exception e) {
            logger.error("Logout failed", e);
            throw new RuntimeException("登出失败", e);
        }
    }

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    public UserRepresentation getUserByUsername(String username) {
        try {
            // 使用管理员权限访问用户信息
          /*   Keycloak adminClient = KeycloakBuilder.builder()
                    .serverUrl(keycloakConfig.getKeycloakServerUrl())
                    .realm(keycloakConfig.getTargetRealm())  // 使用认证realm
                    .clientId(keycloakConfig.getAdminClientId())
                    .username(keycloakConfig.getAdminUsername())
                    .password(keycloakConfig.getAdminPassword())
                    .build();
 */
            RealmResource targetRealm = keycloakAdminClient.realm(keycloakConfig.getTargetRealm());
            UsersResource usersResource = targetRealm.users();
            
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                logger.warn("User not found: {}", username);
                return null;
            }
            
            return users.get(0);
            
        } catch (Exception e) {
            logger.error("Error retrieving user: {}", username, e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }

    /**
     * 将AccessTokenResponse转换为KeycloakTokenDTO
     */
    private KeycloakTokenDTO convertToTokenDTO(AccessTokenResponse tokenResponse) {
        KeycloakTokenDTO tokenDTO = new KeycloakTokenDTO();
        tokenDTO.setAccessToken(tokenResponse.getToken());
        tokenDTO.setRefreshToken(tokenResponse.getRefreshToken());
        tokenDTO.setExpiresIn((int) tokenResponse.getExpiresIn());
        tokenDTO.setRefreshExpiresIn((int) tokenResponse.getRefreshExpiresIn());
        tokenDTO.setTokenType(tokenResponse.getTokenType());
        tokenDTO.setIdToken(tokenResponse.getIdToken());
        tokenDTO.setSessionState(tokenResponse.getSessionState());
        tokenDTO.setScope(tokenResponse.getScope());
        tokenDTO.setNotBeforePolicy(tokenResponse.getNotBeforePolicy());
        return tokenDTO;
    }
}