package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Keycloak UserProfile必需性配置DTO
 * 对应UserProfile属性的required配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileRequiredDTO {

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("scopes")
    private List<String> scopes;

    // 默认构造函数
    public UserProfileRequiredDTO() {
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        return "UserProfileRequiredDTO{" +
                "roles=" + roles +
                ", scopes=" + scopes +
                '}';
    }
}