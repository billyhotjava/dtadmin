package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Keycloak UserProfile选择器配置DTO
 * 对应UserProfile属性的selector配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileSelectorDTO {

    @JsonProperty("scopes")
    private List<String> scopes;

    // 默认构造函数
    public UserProfileSelectorDTO() {
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        return "UserProfileSelectorDTO{" +
                "scopes=" + scopes +
                '}';
    }
}