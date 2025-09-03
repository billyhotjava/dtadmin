package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Keycloak组管理服务
 * 提供组的CRUD操作
 */
@Service
public class KeycloakGroupService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakGroupService.class);
    
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;
    private final KeycloakAuthService authService;

    public KeycloakGroupService(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate,
                               KeycloakConfig keycloakConfig,
                               KeycloakAuthService authService) {
        this.restTemplate = restTemplate;
        this.keycloakConfig = keycloakConfig;
        this.authService = authService;
    }

    /**
     * 获取所有组
     */
    public List<KeycloakGroupDTO> getAllGroups() {
        try {
            String url = String.format("%s/admin/realms/%s/groups",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakGroupDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakGroupDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Successfully retrieved {} groups", 
                        response.getBody() != null ? response.getBody().size() : 0);
                return response.getBody();
            } else {
                logger.error("Failed to retrieve groups, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to retrieve groups from Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error retrieving groups from Keycloak", e);
            throw new RuntimeException("Error retrieving groups from Keycloak", e);
        }
    }

    /**
     * 根据ID获取组
     */
    public KeycloakGroupDTO getGroupById(String groupId) {
        try {
            String url = String.format("%s/admin/realms/%s/groups/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    groupId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<KeycloakGroupDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, KeycloakGroupDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get group by ID, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get group by ID");
            }
        } catch (Exception e) {
            logger.error("Error getting group by ID: {}", groupId, e);
            throw new RuntimeException("Error getting group by ID: " + groupId, e);
        }
    }

    /**
     * 创建新组
     */
    public String createGroup(KeycloakGroupDTO group) {
        try {
            String url = String.format("%s/admin/realms/%s/groups",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm());

            HttpEntity<KeycloakGroupDTO> entity = authService.createAuthEntity(group);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                // 从Location头获取新创建组的ID
                String location = response.getHeaders().getFirst("Location");
                if (location != null) {
                    String groupId = location.substring(location.lastIndexOf('/') + 1);
                    logger.info("Successfully created group: {}", group.getName());
                    return groupId;
                }
                throw new RuntimeException("Group created but could not retrieve group ID");
            } else {
                logger.error("Failed to create group, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create group");
            }
        } catch (Exception e) {
            logger.error("Error creating group: {}", group.getName(), e);
            throw new RuntimeException("Error creating group: " + group.getName(), e);
        }
    }

    /**
     * 更新组信息
     */
    public void updateGroup(String groupId, KeycloakGroupDTO group) {
        try {
            String url = String.format("%s/admin/realms/%s/groups/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    groupId);

            HttpEntity<KeycloakGroupDTO> entity = authService.createAuthEntity(group);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully updated group: {}", groupId);
            } else {
                logger.error("Failed to update group, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to update group");
            }
        } catch (Exception e) {
            logger.error("Error updating group: {}", groupId, e);
            throw new RuntimeException("Error updating group: " + groupId, e);
        }
    }

    /**
     * 删除组
     */
    public void deleteGroup(String groupId) {
        try {
            String url = String.format("%s/admin/realms/%s/groups/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    groupId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully deleted group: {}", groupId);
            } else {
                logger.error("Failed to delete group, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to delete group");
            }
        } catch (Exception e) {
            logger.error("Error deleting group: {}", groupId, e);
            throw new RuntimeException("Error deleting group: " + groupId, e);
        }
    }

    /**
     * 获取组的成员
     */
    public List<String> getGroupMembers(String groupId) {
        try {
            String url = String.format("%s/admin/realms/%s/groups/%s/members",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    groupId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<String>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get group members, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get group members");
            }
        } catch (Exception e) {
            logger.error("Error getting members for group: {}", groupId, e);
            throw new RuntimeException("Error getting members for group: " + groupId, e);
        }
    }

    /**
     * 将用户加入组
     */
    public void addUserToGroup(String groupId, String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/groups/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId,
                    groupId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully added user {} to group {}", userId, groupId);
            } else {
                logger.error("Failed to add user to group, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to add user to group");
            }
        } catch (Exception e) {
            logger.error("Error adding user {} to group {}", userId, groupId, e);
            throw new RuntimeException("Error adding user to group", e);
        }
    }

    /**
     * 将用户从组中移除
     */
    public void removeUserFromGroup(String groupId, String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/groups/%s",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId,
                    groupId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully removed user {} from group {}", userId, groupId);
            } else {
                logger.error("Failed to remove user from group, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to remove user from group");
            }
        } catch (Exception e) {
            logger.error("Error removing user {} from group {}", userId, groupId, e);
            throw new RuntimeException("Error removing user from group", e);
        }
    }

    /**
     * 获取用户所属的组
     */
    public List<KeycloakGroupDTO> getUserGroups(String userId) {
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/groups",
                    keycloakConfig.getKeycloakServerUrl(),
                    keycloakConfig.getRealm(),
                    userId);

            HttpEntity<Void> entity = authService.createAuthEntity();
            
            ResponseEntity<List<KeycloakGroupDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new ParameterizedTypeReference<List<KeycloakGroupDTO>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Failed to get user groups, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get user groups");
            }
        } catch (Exception e) {
            logger.error("Error getting groups for user: {}", userId, e);
            throw new RuntimeException("Error getting groups for user: " + userId, e);
        }
    }
}