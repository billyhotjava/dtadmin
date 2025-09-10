package com.yuzhi.dtadmin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApprovalItem.
 */
@Entity
@Table(name = "approval_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApprovalItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "target_kind", nullable = false)
    private String targetKind;

    @NotNull
    @Column(name = "target_id", nullable = false)
    private String targetId;

    @NotNull
    @Column(name = "seq_number", nullable = false)
    private Integer seqNumber;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "request_id", insertable = false, updatable = false)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @JsonIgnoreProperties(value = { "items" }, allowSetters = true)
    private ApprovalRequest request;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApprovalItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetKind() {
        return this.targetKind;
    }

    public ApprovalItem targetKind(String targetKind) {
        this.setTargetKind(targetKind);
        return this;
    }

    public void setTargetKind(String targetKind) {
        this.targetKind = targetKind;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public ApprovalItem targetId(String targetId) {
        this.setTargetId(targetId);
        return this;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getSeqNumber() {
        return this.seqNumber;
    }

    public ApprovalItem seqNumber(Integer seqNumber) {
        this.setSeqNumber(seqNumber);
        return this;
    }

    public void setSeqNumber(Integer seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getPayload() {
        return this.payload;
    }

    public ApprovalItem payload(String payload) {
        this.setPayload(payload);
        return this;
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

    public ApprovalRequest getRequest() {
        return this.request;
    }

    public void setRequest(ApprovalRequest approvalRequest) {
        this.request = approvalRequest;
    }

    public ApprovalItem request(ApprovalRequest approvalRequest) {
        this.setRequest(approvalRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ApprovalItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApprovalItem{" +
            "id=" + getId() +
            ", targetKind='" + getTargetKind() + "'" +
            ", targetId='" + getTargetId() + "'" +
            ", seqNumber=" + getSeqNumber() +
            ", payload='" + getPayload() + "'" +
            ", requestId=" + (getRequest() != null ? getRequest().getId() : null) +
            "}";
    }
}