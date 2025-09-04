package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Keycloak用户管理服务 - 使用官方Admin Client
 * 提供用户的CRUD操作
 */
@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);
    
    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakUserService(Keycloak keycloakAdminClient, KeycloakConfig keycloakConfig) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 获取目标Realm的资源
     */
    private RealmResource getTargetRealmResource() {
        return keycloakAdminClient.realm(keycloakConfig.getTargetRealm());
    }

    /**
     * 将Keycloak UserRepresentation转换为DTO
     */
    private KeycloakUserDTO convertToDTO(UserRepresentation user) {
        KeycloakUserDTO dto = new KeycloakUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedTimestamp(user.getCreatedTimestamp());
        
        // 添加attributes字段的转换
        dto.setAttributes(user.getAttributes());
        
        // 添加groups、realmRoles、clientRoles字段的转换
        dto.setGroups(user.getGroups());
        dto.setRealmRoles(user.getRealmRoles());
        dto.setClientRoles(user.getClientRoles());
        
        return dto;
    }

    /**
     * 将DTO转换为Keycloak UserRepresentation
     */
    private UserRepresentation convertFromDTO(KeycloakUserDTO dto) {
        UserRepresentation user = new UserRepresentation();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(dto.isEnabled());
        user.setEmailVerified(dto.isEmailVerified());
        return user;
    }

    /**
     * 获取所有用户 - 使用Admin Client
     */
    public List<KeycloakUserDTO> getAllUsers() {
        return getAllUsers(0, 100);
    }

    /**
     * 获取用户列表（分页）- 使用Admin Client
     */
    public List<KeycloakUserDTO> getAllUsers(int first, int max) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UsersResource usersResource = realmResource.users();
            
            // 使用只包含基本参数的查询，避免获取详细的profile metadata
            List<UserRepresentation> users = usersResource.list(first, max);
            
            // 清理可能导致问题的字段
            users.forEach(user -> {
                user.setUserProfileMetadata(null);
            });
            
            logger.debug("Successfully retrieved {} users using Admin Client", users.size());
            
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving users using Admin Client", e);
            throw new RuntimeException("Error retrieving users from Keycloak", e);
        }
    }

    /**
     * 根据用户名搜索用户 - 使用Admin Client
     */
    public List<KeycloakUserDTO> searchUsers(String username) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UsersResource usersResource = realmResource.users();
            
            // 使用基本的搜索方法
            List<UserRepresentation> users = usersResource.search(username);
            
            // 清理可能导致问题的字段
            users.forEach(user -> {
                user.setUserProfileMetadata(null);
            });
            
            logger.debug("Successfully found {} users matching: {}", users.size(), username);
            
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error searching users: {}", username, e);
            throw new RuntimeException("Error searching users in Keycloak", e);
        }
    }

    /**
     * 根据ID获取用户 - 使用Admin Client
     */
    public KeycloakUserDTO getUserById(String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            // 获取基本的用户信息
            UserRepresentation user = userResource.toRepresentation();
            
            // 清理可能导致问题的字段
            user.setUserProfileMetadata(null);
            
            logger.debug("Successfully retrieved user by ID: {}", userId);
            return convertToDTO(user);
            
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", userId, e);
            throw new RuntimeException("Error retrieving user from Keycloak", e);
        }
    }

    /**
     * 创建新用户 - 使用Admin Client
     */
    public String createUser(KeycloakUserDTO userDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UsersResource usersResource = realmResource.users();
            
            UserRepresentation user = convertFromDTO(userDTO);
            
            Response response = usersResource.create(user);
            
            if (response.getStatus() == 201) {
                String userId = extractUserIdFromLocation(response.getLocation().toString());
                logger.info("Successfully created user: {} with ID: {}", userDTO.getUsername(), userId);
                return userId;
            } else {
                logger.error("Failed to create user, status: {}", response.getStatus());
                throw new RuntimeException("Failed to create user in Keycloak");
            }
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", userDTO.getUsername(), e);
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
    }

    /**
     * 从Location头中提取用户ID
     */
    private String extractUserIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    /**
     * 更新用户信息 - 使用Admin Client
     */
    public void updateUser(String userId, KeycloakUserDTO userDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            UserRepresentation user = convertFromDTO(userDTO);
            user.setId(userId); // 确保ID正确
            
            userResource.update(user);
            
            logger.info("Successfully updated user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error updating user: {}", userId, e);
            throw new RuntimeException("Error updating user in Keycloak", e);
        }
    }

    /**
     * 删除用户 - 使用Admin Client
     */
    public void deleteUser(String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            userResource.remove();
            
            logger.info("Successfully deleted user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            throw new RuntimeException("Error deleting user from Keycloak", e);
        }
    }

    /**
     * 重置用户密码 - 使用Admin Client
     */
    public void resetPassword(String userId, String newPassword, boolean temporary) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(temporary);
            
            userResource.resetPassword(credential);
            
            logger.info("Successfully reset password for user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", userId, e);
            throw new RuntimeException("Error resetting user password in Keycloak", e);
        }
    }

    /**
     * 启用/禁用用户 - 使用Admin Client
     */
    public void setUserEnabled(String userId, boolean enabled) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            // 获取基本的用户信息
            UserRepresentation user = userResource.toRepresentation();
            
            // 清理可能导致问题的字段
            user.setUserProfileMetadata(null);
            user.setEnabled(enabled);
            
            userResource.update(user);
            
            logger.info("Successfully {} user: {}", enabled ? "enabled" : "disabled", userId);
            
        } catch (Exception e) {
            logger.error("Error setting user enabled status: {}", userId, e);
            throw new RuntimeException("Error updating user status in Keycloak", e);
        }
    }

    /**
     * 根据用户名搜索用户的别名方法 - 保持向后兼容性
     */
    public List<KeycloakUserDTO> searchUsersByUsername(String username) {
        return searchUsers(username);
    }

    /**
     * 重置用户密码的别名方法 - 保持向后兼容性
     */
    public void resetUserPassword(String userId, String newPassword, Boolean temporary) {
        resetPassword(userId, newPassword, temporary != null ? temporary : false);
    }
}