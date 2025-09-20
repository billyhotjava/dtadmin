package com.yuzhi.dtadmin.service.dto;

import com.yuzhi.dtadmin.domain.enumeration.AuditOutcome;
import java.time.Instant;

public class AuditEventDTO {

    private Long id;
    private Instant timestamp;
    private String actor;
    private String actorRoles;
    private String ip;
    private String userAgent;
    private String action;
    private String resource;
    private AuditOutcome outcome;
    private String detailJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActorRoles() {
        return actorRoles;
    }

    public void setActorRoles(String actorRoles) {
        this.actorRoles = actorRoles;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public AuditOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(AuditOutcome outcome) {
        this.outcome = outcome;
    }

    public String getDetailJson() {
        return detailJson;
    }

    public void setDetailJson(String detailJson) {
        this.detailJson = detailJson;
    }
}
