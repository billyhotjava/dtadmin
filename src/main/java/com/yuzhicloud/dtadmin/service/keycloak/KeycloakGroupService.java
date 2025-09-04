package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakGroupDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Keycloak组管理服务 - 使用官方Admin Client
 * 提供组的CRUD操作
 */
@Service
public class KeycloakGroupService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakGroupService.class);
    
    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakGroupService(Keycloak keycloakAdminClient, KeycloakConfig keycloakConfig) {
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
     * 获取所有组 - 使用Admin Client
     */
    public List<KeycloakGroupDTO> getAllGroups() {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupsResource groupsResource = realmResource.groups();
            
            List<GroupRepresentation> groups = groupsResource.groups();
            
            logger.debug("Successfully retrieved {} groups using Admin Client", groups.size());
            
            return groups.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving groups using Admin Client", e);
            throw new RuntimeException("Error retrieving groups from Keycloak", e);
        }
    }

    /**
     * 根据ID获取组 - 使用Admin Client
     */
    public KeycloakGroupDTO getGroupById(String groupId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupResource groupResource = realmResource.groups().group(groupId);
            
            GroupRepresentation group = groupResource.toRepresentation();
            
            logger.debug("Successfully retrieved group by ID: {}", groupId);
            return convertToDTO(group);
            
        } catch (Exception e) {
            logger.error("Error retrieving group by ID: {}", groupId, e);
            throw new RuntimeException("Error getting group by ID: " + groupId, e);
        }
    }

    /**
     * 创建新组 - 使用Admin Client
     */
    public String createGroup(KeycloakGroupDTO groupDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupsResource groupsResource = realmResource.groups();
            
            GroupRepresentation group = convertFromDTO(groupDTO);
            groupsResource.add(group);
            
            logger.info("Successfully created group: {}", groupDTO.getName());
            
            // 获取创建的组ID
            List<GroupRepresentation> groups = groupsResource.groups(groupDTO.getName(), 0, 1);
            if (!groups.isEmpty()) {
                return groups.get(0).getId();
            }
            
            return null;
            
        } catch (Exception e) {
            logger.error("Error creating group: {}", groupDTO.getName(), e);
            throw new RuntimeException("Error creating group: " + groupDTO.getName(), e);
        }
    }

    /**
     * 更新组 - 使用Admin Client
     */
    public void updateGroup(String groupId, KeycloakGroupDTO groupDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupResource groupResource = realmResource.groups().group(groupId);
            
            GroupRepresentation group = convertFromDTO(groupDTO);
            group.setId(groupId); // 确保ID正确
            groupResource.update(group);
            
            logger.info("Successfully updated group: {}", groupId);
            
        } catch (Exception e) {
            logger.error("Error updating group: {}", groupId, e);
            throw new RuntimeException("Error updating group: " + groupId, e);
        }
    }

    /**
     * 删除组 - 使用Admin Client
     */
    public void deleteGroup(String groupId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupResource groupResource = realmResource.groups().group(groupId);
            
            groupResource.remove();
            
            logger.info("Successfully deleted group: {}", groupId);
            
        } catch (Exception e) {
            logger.error("Error deleting group: {}", groupId, e);
            throw new RuntimeException("Error deleting group: " + groupId, e);
        }
    }

    /**
     * 获取组成员 - 使用Admin Client
     */
    public List<String> getGroupMembers(String groupId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            GroupResource groupResource = realmResource.groups().group(groupId);
            
            return groupResource.members().stream()
                    .map(userRepresentation -> userRepresentation.getId())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving group members: {}", groupId, e);
            throw new RuntimeException("Error retrieving group members: " + groupId, e);
        }
    }

    /**
     * 将用户加入组 - 使用Admin Client
     */
    public void addUserToGroup(String groupId, String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            userResource.joinGroup(groupId);
            
            logger.info("Successfully added user {} to group {}", userId, groupId);
            
        } catch (Exception e) {
            logger.error("Error adding user {} to group {}", userId, groupId, e);
            throw new RuntimeException("Error adding user to group", e);
        }
    }

    /**
     * 将用户从组中移除 - 使用Admin Client
     */
    public void removeUserFromGroup(String groupId, String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            userResource.leaveGroup(groupId);
            
            logger.info("Successfully removed user {} from group {}", userId, groupId);
            
        } catch (Exception e) {
            logger.error("Error removing user {} from group {}", userId, groupId, e);
            throw new RuntimeException("Error removing user from group", e);
        }
    }

    /**
     * 获取用户所属的组 - 使用Admin Client
     */
    public List<KeycloakGroupDTO> getUserGroups(String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            List<GroupRepresentation> groups = userResource.groups();
            
            logger.debug("Successfully retrieved {} groups for user: {}", groups.size(), userId);
            
            return groups.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving user groups for user: {}", userId, e);
            throw new RuntimeException("Error retrieving user groups from Keycloak", e);
        }
    }

    /**
     * 将Keycloak GroupRepresentation转换为DTO
     */
    private KeycloakGroupDTO convertToDTO(GroupRepresentation group) {
        KeycloakGroupDTO dto = new KeycloakGroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setPath(group.getPath());
        dto.setAttributes(group.getAttributes());
        dto.setRealmRoles(group.getRealmRoles());
        dto.setClientRoles(group.getClientRoles());
        
        // 转换子组
        if (group.getSubGroups() != null) {
            List<KeycloakGroupDTO> subGroups = group.getSubGroups().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            dto.setSubGroups(subGroups);
        }
        
        return dto;
    }

    /**
     * 将DTO转换为Keycloak GroupRepresentation
     */
    private GroupRepresentation convertFromDTO(KeycloakGroupDTO dto) {
        GroupRepresentation group = new GroupRepresentation();
        group.setId(dto.getId());
        group.setName(dto.getName());
        group.setPath(dto.getPath());
        group.setAttributes(dto.getAttributes());
        group.setRealmRoles(dto.getRealmRoles());
        group.setClientRoles(dto.getClientRoles());
        
        // 转换子组
        if (dto.getSubGroups() != null) {
            List<GroupRepresentation> subGroups = dto.getSubGroups().stream()
                    .map(this::convertFromDTO)
                    .collect(Collectors.toList());
            group.setSubGroups(subGroups);
        }
        
        return group;
    }
}