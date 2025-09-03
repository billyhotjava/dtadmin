package com.yuzhi.dtadmin.web.rest.keycloak;

import com.yuzhi.dtadmin.config.KeycloakConfig;
import com.yuzhi.dtadmin.dto.keycloak.KeycloakTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak登录认证控制器
 * 提供用户登录和令牌管理功能
 */
@RestController
@RequestMapping("/api/keycloak/auth")
public class KeycloakAuthController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthController.class);

    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;

    public KeycloakAuthController(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                                  KeycloakConfig keycloakConfig) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 用户登录请求DTO
     */
    public static class LoginRequest {
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
     * 刷新令牌请求DTO
     */
    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * 登出请求DTO
     */
    public static class LogoutRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * 登录响应DTO
     */
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private int expiresIn;
        private String tokenType;
        private UserInfo user;

        public LoginResponse(KeycloakTokenDTO token, UserInfo user) {
            this.accessToken = token.getAccessToken();
            this.refreshToken = token.getRefreshToken();
            this.expiresIn = token.getExpiresIn();
            this.tokenType = token.getTokenType();
            this.user = user;
        }

        // Getters and setters
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        
        public int getExpiresIn() { return expiresIn; }
        public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
        
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        
        public UserInfo getUser() { return user; }
        public void setUser(UserInfo user) { this.user = user; }
    }

    /**
     * 用户信息DTO
     */
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean enabled;
        private String[] roles;
        private String[] permissions;

        public UserInfo(String id, String username, String email, String firstName, String lastName, boolean enabled) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.enabled = enabled;
            this.roles = new String[]{"user"}; // 默认角色
            this.permissions = new String[]{"read"}; // 默认权限
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String[] getRoles() { return roles; }
        public void setRoles(String[] roles) { this.roles = roles; }
        
        public String[] getPermissions() { return permissions; }
        public void setPermissions(String[] permissions) { this.permissions = permissions; }
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        logger.info("Keycloak auth controller test endpoint called");
        return ResponseEntity.ok(createSuccessResponse("Keycloak Auth Controller is working!"));
    }

    /**
     * 用户登录接口
     * 
     * @param loginRequest 登录请求，包含用户名和密码
     * @return 登录响应，包含访问令牌和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("User login attempt: {}", loginRequest.getUsername());

            // 构建Keycloak令牌请求URL
            String tokenUrl = String.format("%s/realms/%s/protocol/openid_connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            logger.debug("Keycloak token URL: {}", tokenUrl);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 构建请求参数
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", keycloakConfig.getClientId());
            map.add("username", loginRequest.getUsername());
            map.add("password", loginRequest.getPassword());

            // 如果配置了客户端密钥，添加到请求中
            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // 发送登录请求到Keycloak
            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                KeycloakTokenDTO token = response.getBody();
                
                // 获取用户信息（这里简化处理，实际应该从Keycloak获取详细用户信息）
                UserInfo userInfo = new UserInfo(
                        "user-" + loginRequest.getUsername(), // 临时ID
                        loginRequest.getUsername(),
                        loginRequest.getUsername() + "@example.com", // 临时邮箱
                        "",
                        "",
                        true
                );

                LoginResponse loginResponse = new LoginResponse(token, userInfo);
                
                logger.info("User login successful: {}", loginRequest.getUsername());
                return ResponseEntity.ok(createSuccessResponse(loginResponse));
            } else {
                logger.warn("Login failed for user: {}, status: {}", 
                        loginRequest.getUsername(), response.getStatusCode());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("登录失败：用户名或密码错误"));
            }
        } catch (Exception e) {
            logger.error("Login error for user: {}, error: {}", loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 刷新令牌接口
     * 
     * @param request 刷新令牌请求
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid_connect/token",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "refresh_token");
            map.add("client_id", keycloakConfig.getClientId());
            map.add("refresh_token", request.getRefreshToken());

            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakTokenDTO> response = restTemplate.postForEntity(
                    tokenUrl, httpRequest, KeycloakTokenDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return ResponseEntity.ok(createSuccessResponse(response.getBody()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("令牌刷新失败"));
            }
        } catch (Exception e) {
            logger.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("令牌刷新失败：" + e.getMessage()));
        }
    }

    /**
     * 用户登出接口
     * 
     * @param request 登出请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            String logoutUrl = String.format("%s/realms/%s/protocol/openid_connect/logout",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloakConfig.getClientId());
            map.add("refresh_token", request.getRefreshToken());

            if (keycloakConfig.getClientSecret() != null && !keycloakConfig.getClientSecret().isEmpty()) {
                map.add("client_secret", keycloakConfig.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(map, headers);

            restTemplate.postForEntity(logoutUrl, httpRequest, String.class);
            
            logger.info("User logout successful");
            return ResponseEntity.ok(createSuccessResponse("登出成功"));
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("登出失败：" + e.getMessage()));
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