package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Keycloak UserProfile配置DTO
 * 对应完整的UserProfile配置文件
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileConfigDTO {

    @JsonProperty("attributes")
    private List<UserProfileAttributeDTO> attributes;

    @JsonProperty("groups")
    private List<UserProfileGroupDTO> groups;

    @JsonProperty("unmanagedAttributePolicy")
    private String unmanagedAttributePolicy;

    // 默认构造函数
    public UserProfileConfigDTO() {
    }

    public List<UserProfileAttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<UserProfileAttributeDTO> attributes) {
        this.attributes = attributes;
    }

    public List<UserProfileGroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<UserProfileGroupDTO> groups) {
        this.groups = groups;
    }

    public String getUnmanagedAttributePolicy() {
        return unmanagedAttributePolicy;
    }

    public void setUnmanagedAttributePolicy(String unmanagedAttributePolicy) {
        this.unmanagedAttributePolicy = unmanagedAttributePolicy;
    }

    @Override
    public String toString() {
        return "UserProfileConfigDTO{" +
                "attributes=" + (attributes != null ? attributes.size() : 0) +
                ", groups=" + (groups != null ? groups.size() : 0) +
                ", unmanagedAttributePolicy='" + unmanagedAttributePolicy + '\'' +
                '}';
    }
}