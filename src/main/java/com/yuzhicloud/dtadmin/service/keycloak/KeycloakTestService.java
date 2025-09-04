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
 * Keycloak测试服务 - 用于诊断认证问题
 */
@Service
public class KeycloakTestService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakTestService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;

    public KeycloakTestService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate, 
                               KeycloakConfig keycloakConfig) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 测试管理员登录
     */
    public KeycloakTokenDTO testAdminLogin() {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm()); // 使用master realm

            logger.info("Testing admin login to: {}", tokenUrl);
            logger.info("Admin username: {}", keycloakConfig.getAdminUsername());
            logger.info("Admin client ID: {}", keycloakConfig.getAdminClientId());

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
                logger.info("Admin login successful");
                return response.getBody();
            } else {
                logger.error("Admin login failed, status: {}", response.getStatusCode());
                throw new RuntimeException("管理员登录失败");
            }
        } catch (Exception e) {
            logger.error("Admin login test failed", e);
            throw new RuntimeException("管理员登录测试失败：" + e.getMessage(), e);
        }
    }

    /**
     * 测试用户登录（使用目标realm）
     */
    public KeycloakTokenDTO testUserLogin(String username, String password) {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getTargetRealm()); // 使用目标realm

            logger.info("Testing user login to: {}", tokenUrl);
            logger.info("Username: {}", username);
            logger.info("Client ID: {}", keycloakConfig.getClientId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", keycloakConfig.getClientId());
            map.add("username", username);
            map.add("password", password);

            // 只在有客户端密钥时添加
            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().trim().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
                logger.info("Using client secret");
            } else {
                logger.info("No client secret provided (public client)");
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("User login successful");
                return response.getBody();
            } else {
                logger.error("User login failed, status: {}", response.getStatusCode());
                throw new RuntimeException("用户登录失败");
            }
        } catch (Exception e) {
            logger.error("User login test failed", e);
            throw new RuntimeException("用户登录测试失败：" + e.getMessage(), e);
        }
    }

    /**
     * 测试使用admin-cli客户端进行用户登录
     */
    public KeycloakTokenDTO testUserLoginWithAdminCli(String username, String password) {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getTargetRealm()); // 使用目标realm

            logger.info("Testing user login with admin-cli to: {}", tokenUrl);
            logger.info("Username: {}", username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", "admin-cli");  // 使用admin-cli客户端
            map.add("username", username);
            map.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("User login with admin-cli successful");
                return response.getBody();
            } else {
                logger.error("User login with admin-cli failed, status: {}", response.getStatusCode());
                throw new RuntimeException("使用admin-cli客户端登录失败");
            }
        } catch (Exception e) {
            logger.error("User login with admin-cli test failed", e);
            throw new RuntimeException("使用admin-cli客户端登录测试失败：" + e.getMessage(), e);
        }
    }
}