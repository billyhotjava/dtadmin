package com.yuzhi.dtadmin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApprovalRequest.
 */
@Entity
@Table(name = "approval_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApprovalRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "requester", nullable = false)
    private String requester;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ApprovalType type;

    @Size(max = 512)
    @Column(name = "reason", length = 512)
    private String reason;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status;

    @Column(name = "approver")
    private String approver;

    @Size(max = 512)
    @Column(name = "decision_note", length = 512)
    private String decisionNote;

    @Size(max = 1024)
    @Column(name = "error_message", length = 1024)
    private String errorMessage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "request" }, allowSetters = true)
    private Set<ApprovalItem> items = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApprovalRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequester() {
        return this.requester;
    }

    public ApprovalRequest requester(String requester) {
        this.setRequester(requester);
        return this;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public ApprovalType getType() {
        return this.type;
    }

    public ApprovalRequest type(ApprovalType type) {
        this.setType(type);
        return this;
    }

    public void setType(ApprovalType type) {
        this.type = type;
    }

    public String getReason() {
        return this.reason;
    }

    public ApprovalRequest reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ApprovalRequest createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDecidedAt() {
        return this.decidedAt;
    }

    public ApprovalRequest decidedAt(Instant decidedAt) {
        this.setDecidedAt(decidedAt);
        return this;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public ApprovalStatus getStatus() {
        return this.status;
    }

    public ApprovalRequest status(ApprovalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getApprover() {
        return this.approver;
    }

    public ApprovalRequest approver(String approver) {
        this.setApprover(approver);
        return this;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getDecisionNote() {
        return this.decisionNote;
    }

    public ApprovalRequest decisionNote(String decisionNote) {
        this.setDecisionNote(decisionNote);
        return this;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public ApprovalRequest errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Set<ApprovalItem> getItems() {
        return this.items;
    }

    public void setItems(Set<ApprovalItem> approvalItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setRequest(null));
        }
        if (approvalItems != null) {
            approvalItems.forEach(i -> i.setRequest(this));
        }
        this.items = approvalItems;
    }

    public ApprovalRequest items(Set<ApprovalItem> approvalItems) {
        this.setItems(approvalItems);
        return this;
    }

    public ApprovalRequest addItems(ApprovalItem approvalItem) {
        this.items.add(approvalItem);
        approvalItem.setRequest(this);
        return this;
    }

    public ApprovalRequest removeItems(ApprovalItem approvalItem) {
        this.items.remove(approvalItem);
        approvalItem.setRequest(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((ApprovalRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApprovalRequest{" +
            "id=" + getId() +
            ", requester='" + getRequester() + "'" +
            ", type='" + getType() + "'" +
            ", reason='" + getReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", approver='" + getApprover() + "'" +
            ", decisionNote='" + getDecisionNote() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            "}";
    }
}
