package com.yuzhi.dtadmin.service.dto;

import com.yuzhi.dtadmin.domain.enumeration.ChangeAction;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.domain.enumeration.ChangeStatus;
import java.time.Instant;

public class ChangeRequestDTO {

    private Long id;
    private ChangeResourceType resourceType;
    private String resourceId;
    private ChangeAction action;
    private String payloadJson;
    private String diffJson;
    private ChangeStatus status;
    private String requestedBy;
    private Instant requestedAt;
    private String decidedBy;
    private Instant decidedAt;
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChangeResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ChangeResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ChangeAction getAction() {
        return action;
    }

    public void setAction(ChangeAction action) {
        this.action = action;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getDiffJson() {
        return diffJson;
    }

    public void setDiffJson(String diffJson) {
        this.diffJson = diffJson;
    }

    public ChangeStatus getStatus() {
        return status;
    }

    public void setStatus(ChangeStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
