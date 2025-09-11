package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Keycloak角色分配DTO
 * 用于处理角色分配和移除操作
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakRoleAssignmentDTO {

    private List<KeycloakRoleDTO> roles;

    // 默认构造函数
    public KeycloakRoleAssignmentDTO() {
    }

    // 全参构造函数
    public KeycloakRoleAssignmentDTO(List<KeycloakRoleDTO> roles) {
        this.roles = roles;
    }

    // Getter和Setter方法
    public List<KeycloakRoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<KeycloakRoleDTO> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "KeycloakRoleAssignmentDTO{" +
                "roles=" + roles +
                '}';
    }
}