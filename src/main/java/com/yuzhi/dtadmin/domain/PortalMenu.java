package com.yuzhi.dtadmin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "portal_menu")
public class PortalMenu extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @NotBlank
    @Column(name = "path", nullable = false, length = 256)
    private String path;

    @Column(name = "component", length = 256)
    private String component;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties(value = { "parent", "children" }, allowSetters = true)
    private PortalMenu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("sortOrder ASC")
    @JsonIgnoreProperties(value = { "parent", "children" }, allowSetters = true)
    private Set<PortalMenu> children = new LinkedHashSet<>();

    @Override
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

    public PortalMenu getParent() {
        return parent;
    }

    public void setParent(PortalMenu parent) {
        this.parent = parent;
    }

    public Set<PortalMenu> getChildren() {
        return children;
    }

    public void setChildren(Set<PortalMenu> children) {
        this.children = children;
    }
}
