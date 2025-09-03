package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Keycloak认证服务
 * 负责获取和管理访问令牌
 */
@Service
public class KeycloakAuthService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;
    
    private KeycloakTokenDTO currentToken;
    private long tokenExpiryTime;

    public KeycloakAuthService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate, 
                              KeycloakConfig keycloakConfig) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 获取访问令牌
     * 如果当前令牌未过期，则返回缓存的令牌
     * 否则请求新的令牌
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            return currentToken.getAccessToken();
        }
        
        return refreshAccessToken();
    }

    /**
     * 检查当前令牌是否有效
     */
    private boolean isTokenValid() {
        return currentToken != null && 
               currentToken.getAccessToken() != null && 
               System.currentTimeMillis() < tokenExpiryTime;
    }

    /**
     * 刷新访问令牌
     */
    private String refreshAccessToken() {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid_connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

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
                currentToken = response.getBody();
                // 设置令牌过期时间（提前60秒过期以避免边界情况）
                tokenExpiryTime = System.currentTimeMillis() + 
                        (currentToken.getExpiresIn() - 60) * 1000L;
                
                logger.debug("Successfully obtained access token, expires in {} seconds", 
                        currentToken.getExpiresIn());
                
                return currentToken.getAccessToken();
            } else {
                logger.error("Failed to obtain access token, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to obtain access token from Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error obtaining access token from Keycloak", e);
            throw new RuntimeException("Error obtaining access token from Keycloak", e);
        }
    }

    /**
     * 创建带有认证头的HttpHeaders
     */
    public HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());
        return headers;
    }

    /**
     * 创建带有认证头的HttpEntity
     */
    public <T> HttpEntity<T> createAuthEntity(T body) {
        return new HttpEntity<>(body, createAuthHeaders());
    }

    /**
     * 创建带有认证头的HttpEntity（无body）
     */
    public HttpEntity<Void> createAuthEntity() {
        return new HttpEntity<>(createAuthHeaders());
    }
}