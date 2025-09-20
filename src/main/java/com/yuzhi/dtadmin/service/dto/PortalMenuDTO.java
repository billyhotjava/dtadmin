package com.yuzhi.dtadmin.service.dto;

import java.util.ArrayList;
import java.util.List;

public class PortalMenuDTO {

    private Long id;
    private String name;
    private String path;
    private String component;
    private Integer sortOrder;
    private String metadata;
    private Long parentId;
    private List<PortalMenuDTO> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<PortalMenuDTO> getChildren() {
        return children;
    }

    public void setChildren(List<PortalMenuDTO> children) {
        this.children = children;
    }
}
