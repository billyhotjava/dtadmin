package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Keycloak角色管理服务 - 使用官方Admin Client
 * 提供角色的CRUD操作
 */
@Service
public class KeycloakRoleService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleService.class);
    
    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakRoleService(Keycloak keycloakAdminClient, KeycloakConfig keycloakConfig) {
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
     * 获取所有Realm角色 - 使用Admin Client
     */
    public List<KeycloakRoleDTO> getAllRealmRoles() {
        try {
            RealmResource realmResource = getTargetRealmResource();
            RolesResource rolesResource = realmResource.roles();
            
            List<RoleRepresentation> roles = rolesResource.list();
            
            logger.debug("Successfully retrieved {} realm roles using Admin Client", roles.size());
            
            return roles.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving realm roles using Admin Client", e);
            throw new RuntimeException("Error retrieving realm roles from Keycloak", e);
        }
    }

    /**
     * 将Keycloak RoleRepresentation转换为DTO
     */
    private KeycloakRoleDTO convertToDTO(RoleRepresentation role) {
        KeycloakRoleDTO dto = new KeycloakRoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        // 设置基本属性，不设置 composite 和 clientRole时默认为 false
        dto.setComposite(false);
        dto.setClientRole(false);
        dto.setContainerId(role.getContainerId());
        return dto;
    }

    /**
     * 将DTO转换为Keycloak RoleRepresentation
     */
    private RoleRepresentation convertFromDTO(KeycloakRoleDTO dto) {
        RoleRepresentation role = new RoleRepresentation();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setComposite(dto.isComposite());
        role.setClientRole(dto.isClientRole());
        role.setContainerId(dto.getContainerId());
        return role;
    }

    /**
     * 根据名称获取Realm角色 - 使用Admin Client
     */
    public KeycloakRoleDTO getRealmRoleByName(String roleName) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            RoleResource roleResource = realmResource.roles().get(roleName);
            
            RoleRepresentation role = roleResource.toRepresentation();
            
            logger.debug("Successfully retrieved role by name: {}", roleName);
            return convertToDTO(role);
            
        } catch (Exception e) {
            logger.error("Error retrieving role by name: {}", roleName, e);
            throw new RuntimeException("Error getting realm role by name: " + roleName, e);
        }
    }

    /**
     * 创建新的Realm角色 - 使用Admin Client
     */
    public void createRealmRole(KeycloakRoleDTO roleDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            RolesResource rolesResource = realmResource.roles();
            
            RoleRepresentation role = convertFromDTO(roleDTO);
            rolesResource.create(role);
            
            logger.info("Successfully created realm role: {}", roleDTO.getName());
            
        } catch (Exception e) {
            logger.error("Error creating realm role: {}", roleDTO.getName(), e);
            throw new RuntimeException("Error creating realm role: " + roleDTO.getName(), e);
        }
    }

    /**
     * 更新Realm角色 - 使用Admin Client
     */
    public void updateRealmRole(String roleName, KeycloakRoleDTO roleDTO) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            RoleResource roleResource = realmResource.roles().get(roleName);
            
            RoleRepresentation role = convertFromDTO(roleDTO);
            roleResource.update(role);
            
            logger.info("Successfully updated realm role: {}", roleName);
            
        } catch (Exception e) {
            logger.error("Error updating realm role: {}", roleName, e);
            throw new RuntimeException("Error updating realm role: " + roleName, e);
        }
    }

    /**
     * 删除Realm角色 - 使用Admin Client
     */
    public void deleteRealmRole(String roleName) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            RoleResource roleResource = realmResource.roles().get(roleName);
            
            roleResource.remove();
            
            logger.info("Successfully deleted realm role: {}", roleName);
            
        } catch (Exception e) {
            logger.error("Error deleting realm role: {}", roleName, e);
            throw new RuntimeException("Error deleting realm role: " + roleName, e);
        }
    }

    /**
     * 为用户分配Realm角色 - 使用Admin Client
     */
    public void assignRealmRolesToUser(String userId, List<KeycloakRoleDTO> roleDTOs) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            List<RoleRepresentation> roles = roleDTOs.stream()
                    .map(this::convertFromDTO)
                    .collect(Collectors.toList());
                    
            userResource.roles().realmLevel().add(roles);
            
            logger.info("Successfully assigned {} realm roles to user: {}", roles.size(), userId);
            
        } catch (Exception e) {
            logger.error("Error assigning realm roles to user: {}", userId, e);
            throw new RuntimeException("Error assigning realm roles to user: " + userId, e);
        }
    }

    /**
     * 移除用户的Realm角色 - 使用Admin Client
     */
    public void removeRealmRolesFromUser(String userId, List<KeycloakRoleDTO> roleDTOs) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            List<RoleRepresentation> roles = roleDTOs.stream()
                    .map(this::convertFromDTO)
                    .collect(Collectors.toList());
                    
            userResource.roles().realmLevel().remove(roles);
            
            logger.info("Successfully removed {} realm roles from user: {}", roles.size(), userId);
            
        } catch (Exception e) {
            logger.error("Error removing realm roles from user: {}", userId, e);
            throw new RuntimeException("Error removing realm roles from user: " + userId, e);
        }
    }

    /**
     * 获取用户的Realm角色 - 使用Admin Client
     */
    public List<KeycloakRoleDTO> getUserRealmRoles(String userId) {
        try {
            RealmResource realmResource = getTargetRealmResource();
            UserResource userResource = realmResource.users().get(userId);
            
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listEffective();
            
            logger.debug("Successfully retrieved {} realm roles for user: {}", roles.size(), userId);
            
            return roles.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error retrieving user realm roles", e);
            throw new RuntimeException("Error retrieving user realm roles from Keycloak", e);
        }
    }
}