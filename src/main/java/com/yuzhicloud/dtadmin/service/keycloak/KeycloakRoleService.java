package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Keycloak角色管理服务
 * 提供角色的CRUD操作
 */
@Service
public class KeycloakRoleService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;
    private final KeycloakAuthService authService;

    public KeycloakRoleService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                              KeycloakConfig keycloakConfig,
                              KeycloakAuthService authService) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
        this.authService = authService;
    }

    /**
     * 获取所有Realm角色
     */
    public List<KeycloakRoleDTO> getAllRealmRoles() {
        try {
            String url = String.format("%s/admin/realms/%s/roles",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakRoleDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakRoleDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Successfully retrieved {} realm roles", 
                        response.getBody() != null ? response.getBody().size() : 0);
                return response.getBody();
            } else {
                logger.error("Failed to retrieve realm roles, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to retrieve realm roles from Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error retrieving realm roles from Keycloak", e);
            throw new RuntimeException("Error retrieving realm roles from Keycloak", e);
        }
    }

    /**
     * 根据名称获取Realm角色
     */
    public KeycloakRoleDTO getRealmRoleByName(String roleName) {
        try {
            String url = String.format("%s/admin/realms/%s/roles/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    roleName);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<KeycloakRoleDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, KeycloakRoleDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get realm role by name, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get realm role by name");
            }
        } catch (Exception e) {
            logger.error("Error getting realm role by name: {}", roleName, e);
            throw new RuntimeException("Error getting realm role by name: " + roleName, e);
        }
    }

    /**
     * 创建新的Realm角色
     */
    public void createRealmRole(KeycloakRoleDTO role) {
        try {
            String url = String.format("%s/admin/realms/%s/roles",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpEntity<KeycloakRoleDTO> entity = authService.createAuthEntity(role);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                logger.info("Successfully created realm role: {}", role.getName());
            } else {
                logger.error("Failed to create realm role, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create realm role");
            }
        } catch (Exception e) {
            logger.error("Error creating realm role: {}", role.getName(), e);
            throw new RuntimeException("Error creating realm role: " + role.getName(), e);
        }
    }

    /**
     * 更新Realm角色
     */
    public void updateRealmRole(String roleName, KeycloakRoleDTO role) {
        try {
            String url = String.format("%s/admin/realms/%s/roles/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    roleName);

            HttpEntity<KeycloakRoleDTO> entity = authService.createAuthEntity(role);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully updated realm role: {}", roleName);
            } else {
                logger.error("Failed to update realm role, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to update realm role");
            }
        } catch (Exception e) {
            logger.error("Error updating realm role: {}", roleName, e);
            throw new RuntimeException("Error updating realm role: " + roleName, e);
        }
    }

    /**
     * 删除Realm角色
     */
    public void deleteRealmRole(String roleName) {
        try {
            String url = String.format("%s/admin/realms/%s/roles/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    roleName);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully deleted realm role: {}", roleName);
            } else {
                logger.error("Failed to delete realm role, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to delete realm role");
            }
        } catch (Exception e) {
            logger.error("Error deleting realm role: {}", roleName, e);
            throw new RuntimeException("Error deleting realm role: " + roleName, e);
        }
    }

    /**
     * 为用户分配Realm角色
     */
    public void assignRealmRolesToUser(String userId, List<KeycloakRoleDTO> roles) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<List<KeycloakRoleDTO>> entity = authService.createAuthEntity(roles);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully assigned {} realm roles to user: {}", roles.size(), userId);
            } else {
                logger.error("Failed to assign realm roles to user, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to assign realm roles to user");
            }
        } catch (Exception e) {
            logger.error("Error assigning realm roles to user: {}", userId, e);
            throw new RuntimeException("Error assigning realm roles to user: " + userId, e);
        }
    }

    /**
     * 移除用户的Realm角色
     */
    public void removeRealmRolesFromUser(String userId, List<KeycloakRoleDTO> roles) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<List<KeycloakRoleDTO>> entity = authService.createAuthEntity(roles);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully removed {} realm roles from user: {}", roles.size(), userId);
            } else {
                logger.error("Failed to remove realm roles from user, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to remove realm roles from user");
            }
        } catch (Exception e) {
            logger.error("Error removing realm roles from user: {}", userId, e);
            throw new RuntimeException("Error removing realm roles from user: " + userId, e);
        }
    }

    /**
     * 获取用户的Realm角色
     */
    public List<KeycloakRoleDTO> getUserRealmRoles(String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakRoleDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakRoleDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get user realm roles, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get user realm roles");
            }
        } catch (Exception e) {
            logger.error("Error getting realm roles for user: {}", userId, e);
            throw new RuntimeException("Error getting realm roles for user: " + userId, e);
        }
    }
}