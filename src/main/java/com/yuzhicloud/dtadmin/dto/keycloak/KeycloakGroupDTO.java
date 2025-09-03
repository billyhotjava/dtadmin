package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Keycloak组DTO
 * 用于与Keycloak Admin API交互
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakGroupDTO {

    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("attributes")
    private Map<String, List<String>> attributes;
    
    @JsonProperty("realmRoles")
    private List<String> realmRoles;
    
    @JsonProperty("clientRoles")
    private Map<String, List<String>> clientRoles;
    
    @JsonProperty("subGroups")
    private List<KeycloakGroupDTO> subGroups;

    // 默认构造函数
    public KeycloakGroupDTO() {
    }

    // 构造函数
    public KeycloakGroupDTO(String name, String path) {
        this.name = name;
        this.path = path;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public List<String> getRealmRoles() {
        return realmRoles;
    }

    public void setRealmRoles(List<String> realmRoles) {
        this.realmRoles = realmRoles;
    }

    public Map<String, List<String>> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(Map<String, List<String>> clientRoles) {
        this.clientRoles = clientRoles;
    }

    public List<KeycloakGroupDTO> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<KeycloakGroupDTO> subGroups) {
        this.subGroups = subGroups;
    }

    @Override
    public String toString() {
        return "KeycloakGroupDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}