package com.yuzhi.dtadmin.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.yuzhi.dtadmin.domain.ApprovalItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApprovalItemDTO implements Serializable {

    private Long id;

    @NotNull
    private String targetKind;

    @NotNull
    private String targetId;

    @NotNull
    private Integer seqNumber;

    @Lob
    private String payload;

    private Long requestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetKind() {
        return targetKind;
    }

    public void setTargetKind(String targetKind) {
        this.targetKind = targetKind;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Integer seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalItemDTO)) {
            return false;
        }

        ApprovalItemDTO that = (ApprovalItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
    
    // prettier-ignore
    @Override
    public String toString() {
        return "ApprovalItemDTO{" +
            "id=" + getId() +
            ", targetKind='" + getTargetKind() + "'" +
            ", targetId='" + getTargetId() + "'" +
            ", seqNumber=" + getSeqNumber() +
            ", payload='" + getPayload() + "'" +
            ", requestId=" + (getRequestId() != null ? getRequestId() : "null") +
            "}";
    }
}