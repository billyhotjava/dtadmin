package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Keycloak UserProfile属性定义DTO
 * 对应Keycloak UserProfile配置中的属性定义
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileAttributeDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("validations")
    private Map<String, Object> validations;

    @JsonProperty("annotations")
    private Map<String, Object> annotations;

    @JsonProperty("required")
    private UserProfileRequiredDTO required;

    @JsonProperty("permissions")
    private UserProfilePermissionsDTO permissions;

    @JsonProperty("multivalued")
    private Boolean multivalued;

    @JsonProperty("group")
    private String group;

    @JsonProperty("selector")
    private UserProfileSelectorDTO selector;

    // 默认构造函数
    public UserProfileAttributeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Object> getValidations() {
        return validations;
    }

    public void setValidations(Map<String, Object> validations) {
        this.validations = validations;
    }

    public Map<String, Object> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, Object> annotations) {
        this.annotations = annotations;
    }

    public UserProfileRequiredDTO getRequired() {
        return required;
    }

    public void setRequired(UserProfileRequiredDTO required) {
        this.required = required;
    }

    public UserProfilePermissionsDTO getPermissions() {
        return permissions;
    }

    public void setPermissions(UserProfilePermissionsDTO permissions) {
        this.permissions = permissions;
    }

    public Boolean getMultivalued() {
        return multivalued;
    }

    public void setMultivalued(Boolean multivalued) {
        this.multivalued = multivalued;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public UserProfileSelectorDTO getSelector() {
        return selector;
    }

    public void setSelector(UserProfileSelectorDTO selector) {
        this.selector = selector;
    }

    // 为兼容性添加is方法
    public boolean isMultivalued() {
        return multivalued != null ? multivalued : false;
    }

    @Override
    public String toString() {
        return "UserProfileAttributeDTO{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", multivalued=" + multivalued +
                ", group='" + group + '\'' +
                '}';
    }
}