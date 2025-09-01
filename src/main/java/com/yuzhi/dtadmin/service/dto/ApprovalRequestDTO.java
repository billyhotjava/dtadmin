package com.yuzhi.dtadmin.service.dto;

import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.yuzhi.dtadmin.domain.ApprovalRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApprovalRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private String requester;

    @NotNull
    private ApprovalType type;

    @Size(max = 512)
    private String reason;

    @NotNull
    private Instant createdAt;

    private Instant decidedAt;

    @NotNull
    private ApprovalStatus status;

    private String approver;

    @Size(max = 512)
    private String decisionNote;

    @Size(max = 1024)
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public ApprovalType getType() {
        return type;
    }

    public void setType(ApprovalType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalRequestDTO)) {
            return false;
        }

        ApprovalRequestDTO approvalRequestDTO = (ApprovalRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, approvalRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApprovalRequestDTO{" +
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
