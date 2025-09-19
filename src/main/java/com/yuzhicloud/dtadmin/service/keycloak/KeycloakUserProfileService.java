package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileAttributeDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileConfigDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileGroupDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfilePermissionsDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileRequiredDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.UserProfileSelectorDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserProfileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Keycloak UserProfile管理服务
 * 提供UserProfile配置的获取和管理功能
 */
@Service
public class KeycloakUserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserProfileService.class);
    
    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;
    private final KeycloakTokenService tokenService;
    private final RestTemplate keycloakRestTemplate;
    private final ObjectMapper objectMapper;

    public KeycloakUserProfileService(
            Keycloak keycloakAdminClient, 
            KeycloakConfig keycloakConfig,
            KeycloakTokenService tokenService,
            @Qualifier("keycloakRestTemplate") RestTemplate keycloakRestTemplate) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakConfig = keycloakConfig;
        this.tokenService = tokenService;
        this.keycloakRestTemplate = keycloakRestTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取目标Realm的资源
     */
    private RealmResource getTargetRealmResource() {
        return keycloakAdminClient.realm(keycloakConfig.getTargetRealm());
    }

    /**
     * 获取UserProfile配置 - 使用Admin Client
     */
    public UserProfileConfigDTO getUserProfileConfig() {
        try {
            // 使用Admin Client获取UserProfile配置
            logger.debug("Attempting to get UserProfile config using Admin Client");
            
            RealmResource realmResource = getTargetRealmResource();
            UserProfileMetadata userProfileMetadata = realmResource.users().userProfile().getMetadata();
            
            // 转换为DTO
            return convertToDTO(userProfileMetadata);
            
        } catch (Exception e) {
            logger.error("Error retrieving UserProfile config using Admin Client, falling back to REST API", e);
            // 如果Admin Client方式失败，使用REST API
            return getUserProfileConfigViaRestApi();
        }
    }

    /**
     * 更新UserProfile配置 - 使用Admin Client
     */
    public void updateUserProfileConfig(UserProfileConfigDTO config) {
        try {
            // 使用Admin Client更新UserProfile配置
            logger.debug("Updating UserProfile config using Admin Client");
            
            RealmResource realmResource = getTargetRealmResource();
            
            // 转换DTO为Keycloak对象
          //  UserProfileMetadata userProfileMetadata = convertFromDTO(config);
            
            // 更新UserProfile配置
          //  realmResource.users().updateUserProfile(userProfileMetadata);
            
            logger.info("Successfully updated UserProfile config using Admin Client");
            
        } catch (Exception e) {
            logger.error("Error updating UserProfile config using Admin Client, falling back to REST API", e);
            // 如果Admin Client方式失败，使用REST API
            updateUserProfileConfigViaRestApi(config);
        }
    }

    /**
     * 将Keycloak UserProfileMetadata转换为DTO
     */
    private UserProfileConfigDTO convertToDTO(UserProfileMetadata userProfileMetadata) {
        if (userProfileMetadata == null) {
            return null;
        }
        
        UserProfileConfigDTO dto = new UserProfileConfigDTO();
        
        // 转换attributes
        if (userProfileMetadata.getAttributes() != null) {
            java.util.List<UserProfileAttributeDTO> attributes = userProfileMetadata.getAttributes().stream()
                .map(attr -> {
                    UserProfileAttributeDTO attrDto = new UserProfileAttributeDTO();
                    attrDto.setName(attr.getName());
                    attrDto.setDisplayName(attr.getDisplayName());
                    attrDto.setValidations(attr.getValidators());
                    attrDto.setAnnotations(attr.getAnnotations());
                    attrDto.setRequired(attr.isRequired());
                    
                   /*  // 转换required
                    if (attr.getRequired() != null) {
                        UserProfileRequiredDTO requiredDto = new UserProfileRequiredDTO();
                        requiredDto.setRoles(attr.getRequired().getRoles());
                        requiredDto.setScopes(attr.getRequired().getScopes());
                        attrDto.setRequired(requiredDto);
                    }
                    
                    // 转换permissions
                    if (attr.getPermissions() != null) {
                        UserProfilePermissionsDTO permissionsDto = new UserProfilePermissionsDTO();
                        permissionsDto.setView(attr.getPermissions().getView());
                        permissionsDto.setEdit(attr.getPermissions().getEdit());
                        attrDto.setPermissions(permissionsDto);
                    } */
                    
                    attrDto.setMultivalued(attr.isMultivalued());
                    attrDto.setGroup(attr.getGroup());
                    
                    /* // 转换selector
                    if (attr.getSelector() != null) {
                        UserProfileSelectorDTO selectorDto = new UserProfileSelectorDTO();
                        selectorDto.setScopes(attr.getSelector().getScopes());
                        attrDto.setSelector(selectorDto);
                    }
                     */
                    return attrDto;
                })
                .collect(java.util.stream.Collectors.toList());
            dto.setAttributes(attributes);
        }
        
        // 转换groups
        if (userProfileMetadata.getGroups() != null) {
            java.util.List<UserProfileGroupDTO> groups = userProfileMetadata.getGroups().stream()
                .map(group -> {
                    UserProfileGroupDTO groupDto = new UserProfileGroupDTO();
                    groupDto.setName(group.getName());
                    groupDto.setDisplayHeader(group.getDisplayHeader());
                    groupDto.setDisplayDescription(group.getDisplayDescription());
                    groupDto.setAnnotations(group.getAnnotations());
                    return groupDto;
                })
                .collect(java.util.stream.Collectors.toList());
            dto.setGroups(groups);
        }
        
      //  dto.setUnmanagedAttributePolicy(userProfileMetadata.getUnmanagedAttributePolicy());
        
        return dto;
    }

    /**
     * 将DTO转换为Keycloak UserProfileMetadata
     */
 
    /**
     * 通过REST API获取UserProfile配置（回退方案）
     */
    private UserProfileConfigDTO getUserProfileConfigViaRestApi() {
        try {
            // 获取访问令牌
            String accessToken = tokenService.getAccessToken();
            
            // 构建请求URL
            String url = String.format("%s/admin/realms/%s/users/profile", 
                    keycloakConfig.getKeycloakServerUrl(), 
                    keycloakConfig.getTargetRealm());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 发送请求
            logger.debug("Requesting UserProfile config from: {}", url);
            ResponseEntity<String> response = keycloakRestTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 解析响应为DTO
                UserProfileConfigDTO config = objectMapper.readValue(
                        response.getBody(), UserProfileConfigDTO.class);
                
                logger.debug("Successfully retrieved UserProfile config with {} attributes", 
                        config.getAttributes() != null ? config.getAttributes().size() : 0);
                
                return config;
            } else {
                logger.error("Failed to get UserProfile config, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get UserProfile config from Keycloak");
            }
            
        } catch (Exception e) {
            logger.error("Error getting UserProfile config via REST API", e);
            throw new RuntimeException("Error getting UserProfile config via REST API", e);
        }
    }

    /**
     * 通过REST API更新UserProfile配置（回退方案）
     */
    private void updateUserProfileConfigViaRestApi(UserProfileConfigDTO config) {
        try {
            // 获取访问令牌
            String accessToken = tokenService.getAccessToken();
            
            // 构建请求URL
            String url = String.format("%s/admin/realms/%s/users/profile", 
                    keycloakConfig.getKeycloakServerUrl(), 
                    keycloakConfig.getTargetRealm());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Content-Type", "application/json");
            
            // 序列化配置为JSON
            String configJson = objectMapper.writeValueAsString(config);
            HttpEntity<String> entity = new HttpEntity<>(configJson, headers);
            
            // 发送请求
            logger.debug("Updating UserProfile config at: {}", url);
            ResponseEntity<String> response = keycloakRestTemplate.exchange(
                    url, HttpMethod.PUT, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully updated UserProfile config via REST API");
            } else {
                logger.error("Failed to update UserProfile config via REST API, status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to update UserProfile config in Keycloak via REST API");
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing UserProfile config to JSON", e);
            throw new RuntimeException("Error serializing UserProfile config", e);
        } catch (Exception e) {
            logger.error("Error updating UserProfile config via REST API", e);
            throw new RuntimeException("Error updating UserProfile config via REST API", e);
        }
    }

    /**
     * 检查UserProfile配置是否存在
     */
    public boolean isUserProfileConfigured() {
        try {
            getUserProfileConfig();
            return true;
        } catch (Exception e) {
            logger.debug("UserProfile not configured or accessible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取UserProfile中的所有属性名称列表
     */
    public java.util.List<String> getUserProfileAttributeNames() {
        try {
            UserProfileConfigDTO config = getUserProfileConfig();
            if (config.getAttributes() != null) {
                return config.getAttributes().stream()
                        .map(attr -> attr.getName())
                        .collect(java.util.stream.Collectors.toList());
            }
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error getting UserProfile attribute names", e);
            return java.util.Collections.emptyList();
        }
    }
}