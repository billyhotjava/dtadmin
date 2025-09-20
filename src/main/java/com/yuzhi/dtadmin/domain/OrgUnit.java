package com.yuzhi.dtadmin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuzhi.dtadmin.domain.enumeration.OrgUnitStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "org_unit")
public class OrgUnit extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrgUnitStatus status = OrgUnitStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties(value = { "parent", "children" }, allowSetters = true)
    private OrgUnit parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnoreProperties(value = { "parent", "children" }, allowSetters = true)
    private Set<OrgUnit> children = new HashSet<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrgUnitStatus getStatus() {
        return status;
    }

    public void setStatus(OrgUnitStatus status) {
        this.status = status;
    }

    public OrgUnit getParent() {
        return parent;
    }

    public void setParent(OrgUnit parent) {
        this.parent = parent;
    }

    public Set<OrgUnit> getChildren() {
        return children;
    }

    public void setChildren(Set<OrgUnit> children) {
        this.children = children;
    }
}
