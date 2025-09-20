package com.yuzhi.dtadmin.domain;

import com.yuzhi.dtadmin.domain.enumeration.AuditOutcome;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "audit_event")
public class AuditEvent extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "event_ts", nullable = false)
    private Instant timestamp = Instant.now();

    @Column(name = "actor", length = 128)
    private String actor;

    @Column(name = "actor_roles", length = 256)
    private String actorRoles;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "action", length = 128)
    private String action;

    @Column(name = "resource", length = 128)
    private String resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 32)
    private AuditOutcome outcome = AuditOutcome.SUCCESS;

    @Lob
    @Column(name = "detail_json", columnDefinition = "TEXT")
    private String detailJson;

    @Override
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
