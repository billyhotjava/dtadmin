package com.yuzhi.dtadmin.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ExternalResource.
 */
@Entity
@Table(name = "external_resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExternalResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "urn", nullable = false, unique = true)
    private String urn;

    @NotNull
    @Column(name = "max_level", nullable = false)
    private String maxLevel;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExternalResource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrn() {
        return this.urn;
    }

    public ExternalResource urn(String urn) {
        this.setUrn(urn);
        return this;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getMaxLevel() {
        return this.maxLevel;
    }

    public ExternalResource maxLevel(String maxLevel) {
        this.setMaxLevel(maxLevel);
        return this;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalResource)) {
            return false;
        }
        return getId() != null && getId().equals(((ExternalResource) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExternalResource{" +
            "id=" + getId() +
            ", urn='" + getUrn() + "'" +
            ", maxLevel='" + getMaxLevel() + "'" +
            "}";
    }
}
