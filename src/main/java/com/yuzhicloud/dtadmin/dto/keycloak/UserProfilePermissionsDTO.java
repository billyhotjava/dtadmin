package com.yuzhicloud.dtadmin.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Keycloak UserProfile权限配置DTO
 * 对应UserProfile属性的permissions配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfilePermissionsDTO {

    @JsonProperty("view")
    private List<String> view;

    @JsonProperty("edit")
    private List<String> edit;

    // 默认构造函数
    public UserProfilePermissionsDTO() {
    }

    public List<String> getView() {
        return view;
    }

    public void setView(List<String> view) {
        this.view = view;
    }

    public List<String> getEdit() {
        return edit;
    }

    public void setEdit(List<String> edit) {
        this.edit = edit;
    }

    @Override
    public String toString() {
        return "UserProfilePermissionsDTO{" +
                "view=" + view +
                ", edit=" + edit +
                '}';
    }
}