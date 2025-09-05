package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Keycloak UserProfile组配置DTO
 * 对应UserProfile中的组定义
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileGroupDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayHeader")
    private String displayHeader;

    @JsonProperty("displayDescription")
    private String displayDescription;

    @JsonProperty("annotations")
    private Map<String, Object> annotations;

    // 默认构造函数
    public UserProfileGroupDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayHeader() {
        return displayHeader;
    }

    public void setDisplayHeader(String displayHeader) {
        this.displayHeader = displayHeader;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public Map<String, Object> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, Object> annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "UserProfileGroupDTO{" +
                "name='" + name + '\'' +
                ", displayHeader='" + displayHeader + '\'' +
                ", displayDescription='" + displayDescription + '\'' +
                '}';
    }
}