package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakTokenDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakAuthService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakTokenService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak登录认证控制器 - 使用官方Admin Client SDK
 * 提供用户登录和令牌管理功能
 */
@RestController
@RequestMapping("/api/keycloak/auth")
public class KeycloakAuthController {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthController.class);

    private final KeycloakAuthService authService;
    private final KeycloakTokenService tokenService;
    private final KeycloakUserService userService;

    public KeycloakAuthController(KeycloakAuthService authService, 
                                  KeycloakTokenService tokenService,
                                  KeycloakUserService userService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.userService = userService;
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
     * 用户登录接口 - 使用官方Admin Client SDK
     * 
     * @param loginRequest 登录请求，包含用户名和密码
     * @return 登录响应，包含访问令牌和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("User login attempt using Admin Client SDK: {}", loginRequest.getUsername());

            // 使用官方SDK进行认证
            KeycloakTokenDTO token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            // 获取用户信息
            UserRepresentation userRep = authService.getUserByUsername(loginRequest.getUsername());
            UserInfo userInfo;
            
            if (userRep != null) {
                userInfo = new UserInfo(
                    userRep.getId(),
                    userRep.getUsername(),
                    userRep.getEmail(),
                    userRep.getFirstName(),
                    userRep.getLastName(),
                    userRep.isEnabled()
                );
            } else {
                // 如果获取不到用户信息，使用默认值
                userInfo = new UserInfo(
                    "user-" + loginRequest.getUsername(),
                    loginRequest.getUsername(),
                    loginRequest.getUsername() + "@example.com",
                    "",
                    "",
                    true
                );
            }

            LoginResponse loginResponse = new LoginResponse(token, userInfo);
            
            logger.info("User login successful using Admin Client SDK: {}", loginRequest.getUsername());
            return ResponseEntity.ok(createSuccessResponse(loginResponse));
            
        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 刷新令牌接口 - 使用REST API实现
     * 
     * @param request 刷新令牌请求
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        try {
            logger.info("Token refresh attempt using REST API");
            
            // 使用REST API刷新令牌
            KeycloakTokenDTO token = tokenService.refreshToken(request.getRefreshToken());
            
            logger.info("Token refresh successful using REST API");
            return ResponseEntity.ok(createSuccessResponse(token));
            
        } catch (Exception e) {
            logger.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("令牌刷新失败：" + e.getMessage()));
        }
    }

    /**
     * 用户登出接口 - 使用REST API实现
     * 
     * @param request 登出请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            logger.info("User logout attempt using REST API");
            
            // 使用REST API执行登出
            tokenService.logout(request.getRefreshToken());
            
            logger.info("User logout successful using REST API");
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