package com.yuzhi.dtadmin.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.yuzhi.dtadmin.domain.ExternalResource} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExternalResourceDTO implements Serializable {

    private Long id;

    @NotNull
    private String urn;

    @NotNull
    private String maxLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalResourceDTO)) {
            return false;
        }

        ExternalResourceDTO externalResourceDTO = (ExternalResourceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, externalResourceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExternalResourceDTO{" +
            "id=" + getId() +
            ", urn='" + getUrn() + "'" +
            ", maxLevel='" + getMaxLevel() + "'" +
            "}";
    }
}
