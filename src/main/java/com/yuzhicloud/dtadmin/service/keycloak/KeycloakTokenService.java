package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Keycloak令牌服务 - 支持令牌刷新和登出的REST API实现
 * 用于补充官方Admin Client SDK的功能限制
 */
@Service
public class KeycloakTokenService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakTokenService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;

    public KeycloakTokenService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate, 
                                KeycloakConfig keycloakConfig) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 获取管理员访问令牌 - 使用REST API实现
     * 
     * @return 访问令牌
     */
    public String getAccessToken() {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm()); // 使用auth-realm而不是target-realm

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", keycloakConfig.getAdminClientId());
            map.add("username", keycloakConfig.getAdminUsername());
            map.add("password", keycloakConfig.getAdminPassword());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Admin token acquisition successful using REST API");
                return response.getBody().getAccessToken();
            } else {
                logger.warn("Admin token acquisition failed, status: {}", response.getStatusCode());
                throw new RuntimeException("获取管理员令牌失败");
            }
        } catch (Exception e) {
            logger.error("Admin token acquisition error using REST API", e);
            throw new RuntimeException("获取管理员令牌失败：" + e.getMessage(), e);
        }
    }

    /**
     * 刷新令牌 - 使用REST API实现
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public KeycloakTokenDTO refreshToken(String refreshToken) {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getTargetRealm());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "refresh_token");
            map.add("client_id", keycloakConfig.getClientId());
            map.add("refresh_token", refreshToken);

            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Token refresh successful using REST API");
                return response.getBody();
            } else {
                logger.warn("Token refresh failed, status: {}", response.getStatusCode());
                throw new RuntimeException("令牌刷新失败");
            }
        } catch (Exception e) {
            logger.error("Token refresh error using REST API", e);
            throw new RuntimeException("令牌刷新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 用户登出 - 使用REST API实现
     * 
     * @param refreshToken 刷新令牌
     */
    public void logout(String refreshToken) {
        try {
            String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getTargetRealm());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloakConfig.getClientId());
            map.add("refresh_token", refreshToken);

            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            restTemplate.postForEntity(logoutUrl, request, String.class);
            
            logger.info("User logout successful using REST API");
            
        } catch (Exception e) {
            logger.error("Logout error using REST API", e);
            throw new RuntimeException("登出失败：" + e.getMessage(), e);
        }
    }
}